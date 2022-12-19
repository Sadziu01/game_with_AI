package pwr.sadowski.operators;

import pwr.sadowski.GUI.MyFrame;
import pwr.sadowski.variables.OObject;
import pwr.sadowski.variables.Position;
import pwr.sadowski.SocketControler;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Thread.sleep;
import static pwr.sadowski.GUI.MyFrame.hostPanel;
import static pwr.sadowski.GUI.MyFrame.playerPanel;

public class Player extends SocketControler {
    private static final Position cords = new Position(100, 150);
    private static final OObject[][] map = new OObject[cords.getY()][cords.getX()];


    private static final ArrayList<Position> boxes = new ArrayList<>();
    private static final ArrayList<Position> fogs = new ArrayList<>();

    private int portToSend;
    int score;

    public void setPortToSend(int portToSend) throws IOException {
        this.portToSend = portToSend;
        port = 1000;
        s = new ServerSocket(port);
        send("fls", String.valueOf(port), "localhost", portToSend);

        String[] splitedArray;
        splitedArray = receive().split(";");

        var flag = splitedArray[0];

        if(flag.equals("flb")) {
            port = Integer.parseInt(splitedArray[1]);
            s = new ServerSocket(port);

            cords.setY(Integer.parseInt(splitedArray[2]));
            cords.setX(Integer.parseInt(splitedArray[3]));

            map[cords.getY()][cords.getX()].setContent('P');
            playerPanel.repaint();
        }
    }

    public Player(){
        map();
    }

    private void map(){
        for(int y = 0; y < 100; y++){
            for (int x = 0; x < 150; x++) {
                map[y][x] = new OObject(' ', 0);
            }
        }
        playerPanel.setMap(map);
    }

    public void start(){
        Thread t = new Thread(() -> {
            while (true){
                see();
                take();
                see();
                for (int i = 0; i < 100; i++) {
                    for (int j = 0; j < 150; j++) {
                        if (map[i][j].getContent() == 'T') {
                            boxes.add(new Position(i, j));
                        } else if (map[i][j].getContent() == ' ') {
                            fogs.add(new Position(i, j));
                        }
                    }
                }

                //TODO skrzynki pierwsze
                Position closestObject = findClosestBox(cords, fogs);

                if(!boxes.isEmpty()){
                    closestObject = findClosestBox(cords, boxes);
                }

                ArrayList<Position> path = findShortestPath(cords, closestObject);

                Collections.reverse(path);
                path.add(closestObject);
                path.remove(0);

                for (Position p : path) {
                    System.out.println(cords);
                    moving(p);
                    take();
                    see();

                    if (fogs.contains(p)) {
                        fogs.remove(p);
                        System.out.println("Gracz dotarł do teren w punkcie: " + p);
                        break;
                    }
                }
                boxes.clear();
                fogs.clear();
            }
        });
        t.start();
    }

    private void see(){
        String messToSendToHost = port + ";" + cords.getY() + ";" + cords.getX();
        send("ss", messToSendToHost, "localhost", portToSend);

        String[] splittedArray;
        splittedArray = receive().split(";");

        var flag = splittedArray[0];

        if(flag.equals("sb")) {
            int k = 1;
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if ((cords.getY() + i) >= 0 && (cords.getY() + i) < 100 && (cords.getX() + j) >= 0 && (cords.getX() + j) < 150) {
                        map[cords.getY() + i][cords.getX() + j].setContent(splittedArray[k].charAt(0));
                        k++;
                        map[cords.getY() + i][cords.getX() + j].setWaitTime(Integer.parseInt(splittedArray[k]));
                        k++;
                    }
                    else {
                        k += 2;
                    }


                }
            }

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 150; j++) {
                    if(map[i][j].getContent() == 'P' && i != cords.getY() && j != cords.getX() && i != cords.getY()-1 && j != cords.getX()-1 && i != cords.getY()-1 && j != cords.getX()
                            && i != cords.getY()-1 && j != cords.getX()+1 && i != cords.getY() && j != cords.getX()-1 && i != cords.getY() && j != cords.getX() +1 && i != cords.getY()+1 && j != cords.getX()-1
                            && i != cords.getY()+1 && j != cords.getX() && i != cords.getY()-1 && j != cords.getX()-1){
                        map[i][j].setContent('E');
                    }
                }
            }
        }
        playerPanel.repaint();
    }

    private void moving(Position pos) {
        String tempMess = port + ";" + cords.getY() + ";" + cords.getX() + ";" + pos.getY() + ";" + pos.getX();
        send("ms", tempMess, "localhost", portToSend);

        String[] splitedArray;
        splitedArray = receive().split(";");

        var flag = splitedArray[0];

        if(flag.equals("mb")) {
            String result = splitedArray[1];

            if (result.equals("T")) {
                cords.setY(Integer.parseInt(splitedArray[2]));
                cords.setX(Integer.parseInt(splitedArray[3]));
                see();
            }
            try {
                playerPanel.repaint();
                sleep(map[cords.getY()][cords.getX()].getWaitTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void take(){
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if((cords.getY() + i) >= 0 && (cords.getY() + i) < 100 && (cords.getX() + j) >= 0 && (cords.getX() + j) < 150) {
                    if (map[cords.getY() + i][cords.getX() + j].getContent() == 'T') {
                        String messToSendToHost = port + ";" + cords.getY() + ";" + cords.getX() + ";" + (cords.getY() + i) + ";" + (cords.getX() + j);
                        send("ts", messToSendToHost, "localhost", portToSend);

                        String[] splittedArray;
                        splittedArray = receive().split(";");

                        var flag = splittedArray[0];

                        if(flag.equals("tb")){
                            String result = splittedArray[1];
                            score = Integer.parseInt(splittedArray[4]);

                            if (result.equals("T")) {
                                Position toTakeTresure = new Position(cords.getY() + i, cords.getX() + j);
                                moving(toTakeTresure);
                                if((cords.getY() + i) >= 0 && (cords.getY() + i) < 100 && (cords.getX() + j) >= 0 && (cords.getX() + j) < 150) {
                                    map[cords.getY() + i][cords.getX() + j].setContent('P');
                                }

                                map[Integer.parseInt(splittedArray[2])][Integer.parseInt(splittedArray[3])].setContent('E');
                                MyFrame.lblPoints.setText("POINTS: " + score);
                                playerPanel.repaint();
                            }
                        }
                    }
                }
            }
        }
    }

    public static Position findClosestBox(Position currPos, ArrayList<Position> boxes) {
        Position closestBox = null;
        double minDist = Double.MAX_VALUE;

        for (Position p : boxes) {
            double dist = Math.sqrt(Math.pow(currPos.getX() - p.getX(), 2) + Math.pow(currPos.getY() - p.getY(), 2));
            if (dist < minDist) {
                minDist = dist;
                closestBox = p;
            }
        }
        return closestBox;
    }

    public static void isClear(int[][] visited, ArrayList<Position> queue, Position direction, Position current) {
        if((direction.getY()) >= 0 && (direction.getY()) < 100 && (direction.getX()) >= 0 && (direction.getX()) < 150){
            if (map[direction.getY()][direction.getX()].getContent() != 'R' && visited[direction.getY()][direction.getX()] == -1) {
                queue.add(direction);
                visited[direction.getY()][direction.getX()] = visited[current.getY()][current.getX()] + 1;
            }
        }
    }

    public static ArrayList<Position> findShortestPath(Position start, Position end) {
        ArrayList<Position> path = new ArrayList<>();

        int[][] visited = new int[100][150];

        Position tempEnd = end;

        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[0].length; j++) {
                visited[i][j] = -1;
            }
        }

        ArrayList<Position> queue = new ArrayList<>();
        queue.add(start);
        visited[start.getY()][start.getX()] = 0;

        //Wyznaczanie długosci
        while (!queue.isEmpty()) {
            Position current = queue.remove(0);

            Position up = new Position(current.getY(), current.getX() - 1);
            Position down = new Position(current.getY(), current.getX() + 1);
            Position left = new Position(current.getY() - 1, current.getX());
            Position right = new Position(current.getY() + 1, current.getX());
            Position upLeft = new Position(current.getY() - 1, current.getX() - 1);
            Position upRight = new Position(current.getY() + 1, current.getX() - 1);
            Position downLeft = new Position(current.getY() - 1, current.getX() + 1);
            Position downRight = new Position(current.getY() + 1, current.getX() + 1);

            if (current.getY() == tempEnd.getY() && current.getX() == tempEnd.getX()) {
                break;
            }

            isClear(visited, queue, up, current);
            isClear(visited, queue, down, current);
            isClear(visited, queue, left, current);
            isClear(visited, queue, right, current);
            isClear(visited, queue, upLeft, current);
            isClear(visited, queue, upRight, current);
            isClear(visited, queue, downLeft, current);
            isClear(visited, queue, downRight, current);
        }

        //Najkrótszą drogę do skrzynki
        while (tempEnd.getY() != start.getY() || tempEnd.getX() != start.getX()) {
            int minDistance = Integer.MAX_VALUE;
            Position minPoint = new Position(1, 1);

            Position up = new Position(tempEnd.getY(), tempEnd.getX() - 1);
            Position down = new Position(tempEnd.getY(), tempEnd.getX() + 1);
            Position left = new Position(tempEnd.getY() - 1, tempEnd.getX());
            Position right = new Position(tempEnd.getY() + 1, tempEnd.getX() + 1);
            Position upLeft = new Position(tempEnd.getY() - 1, tempEnd.getX() - 1);
            Position upRight = new Position(tempEnd.getY() + 1, tempEnd.getX() - 1);
            Position downLeft = new Position(tempEnd.getY() - 1, tempEnd.getX() + 1);
            Position downRight = new Position(tempEnd.getY() + 1, tempEnd.getX() + 1);

            if((up.getY()) >= 0 && (up.getY()) < 100 && (up.getX()) >= 0 && (up.getX()) < 150) {
                if (visited[up.getY()][up.getX()] >= 0 && visited[up.getY()][up.getX()] < minDistance) {
                    minDistance = visited[up.getY()][up.getX()];
                    minPoint = up;
                }
            }
            if((down.getY()) >= 0 && (down.getY()) < 100 && (down.getX()) >= 0 && (down.getX()) < 150) {
                if (visited[down.getY()][down.getX()] >= 0 && visited[down.getY()][down.getX()] < minDistance) {
                    minDistance = visited[down.getY()][down.getX()];
                    minPoint = down;
                }
            }
            if((left.getY()) >= 0 && (left.getY()) < 100 && (left.getX()) >= 0 && (left.getX()) < 150) {
                if (visited[left.getY()][left.getX()] >= 0 && visited[left.getY()][left.getX()] < minDistance) {
                    minDistance = visited[left.getY()][left.getX()];
                    minPoint = left;
                }
            }
            if((right.getY()) >= 0 && (right.getY()) < 100 && (right.getX()) >= 0 && (right.getX()) < 150) {
                if (visited[right.getY()][right.getX()] >= 0 && visited[right.getY()][right.getX()] < minDistance) {
                    minDistance = visited[right.getY()][right.getX()];
                    minPoint = right;
                }
            }
            if((upLeft.getY()) >= 0 && (upLeft.getY()) < 100 && (upLeft.getX()) >= 0 && (upLeft.getX()) < 150) {
                if (visited[upLeft.getY()][upLeft.getX()] >= 0 && visited[upLeft.getY()][upLeft.getX()] < minDistance) {
                    minDistance = visited[upLeft.getY()][upLeft.getX()];
                    minPoint = upLeft;
                }
            }
            if((upRight.getY()) >= 0 && (upRight.getY()) < 100 && (upRight.getX()) >= 0 && (upRight.getX()) < 150) {
                if (visited[upRight.getY()][upRight.getX()] >= 0 && visited[upRight.getY()][upRight.getX()] < minDistance) {
                    minDistance = visited[upRight.getY()][upRight.getX()];
                    minPoint = upRight;
                }
            }
            if((downLeft.getY()) >= 0 && (downLeft.getY()) < 100 && (downLeft.getX()) >= 0 && (downLeft.getX()) < 150) {
                if (visited[downLeft.getY()][downLeft.getX()] >= 0 && visited[downLeft.getY()][downLeft.getX()] < minDistance) {
                    minDistance = visited[downLeft.getY()][downLeft.getX()];
                    minPoint = downLeft;
                }
            }
            if((downRight.getY()) >= 0 && (downRight.getY()) < 100 && (downRight.getX()) >= 0 && (downRight.getX()) < 150) {
                if (visited[downRight.getY()][downRight.getX()] >= 0 && visited[downRight.getY()][downRight.getX()] < minDistance) {
                    minDistance = visited[downRight.getY()][downRight.getX()];
                    minPoint = downRight;
                }
            }

            path.add(minPoint);
            tempEnd = minPoint;

        }

        return path;
    }

}
