package pwr.sadowski.operators;

import pwr.sadowski.variables.OObject;
import pwr.sadowski.variables.Position;
import pwr.sadowski.SocketControler;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static pwr.sadowski.GUI.MyFrame.hostPanel;

public class Host extends SocketControler {
    private HashSet<Integer> uniquePorts = new HashSet<>();
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();


    private final Position cords = new Position(100, 150);
    private final OObject[][] map = new OObject[cords.getY()][cords.getX()];

    public void setPort(int port) throws IOException {
        s = new ServerSocket(port);
    }

    public Host() {
        map_initialization();
    }

    private void map_initialization(){
        for(int y = 0; y < 100; y++){
            for (int x = 0; x < 150; x++) {
                map[y][x] = new OObject('E', 500);
            }
        }
        rock_generate(1000);
        tresure_generate(400);

        hostPanel.setMap(map);
    }

    private void rock_generate(int amount){
        for (int i = 0; i < amount; i++) {
            int y = new Random().nextInt(cords.getY());
            int x = new Random().nextInt(cords.getX());

            if(map[y][x].getContent() == 'E'){
                map[y][x].setContent('R');
                map[y][x].setWaitTime(0);
            }
            else {
                rock_generate(1);
            }
        }
    }

    private void tresure_generate(int amount){
        for (int i = 0; i < amount; i++) {
            int y = new Random().nextInt(cords.getY());
            int x = new Random().nextInt(cords.getX());

            if(map[y][x].getContent() == 'E'){
                map[y][x].setContent('T');
                map[y][x].setWaitTime((new Random().nextInt(5000 + 1000) + 5000));
            }
            else {
                tresure_generate(1);
            }
        }
    }

    public void start(){
        Thread listen = new Thread(() -> {
            while (true) {
                queue.add(receive());
            }
        });
        listen.start();

        Thread operations = new Thread(() -> {
            while(true){
                try{
                    String flag;
                    int portToSend;
                    String messageToSend;

                    String theLine = queue.take();

                    String[] splittedArray;
                    splittedArray = theLine.split(";");

                    flag = splittedArray[0];
                    portToSend = Integer.parseInt(splittedArray[1]);


                    switch (flag) {
                        case "fls" ->{

                        int personalPort = createUniquePort();

                        var newPosition = new Position(new Random().nextInt(cords.getY()), new Random().nextInt(cords.getX()));

                        emptyPlace(newPosition);

                        map[newPosition.getY()][newPosition.getX()].setContent('P');

                        hostPanel.repaint();

                        messageToSend = personalPort + ";" + newPosition.getY() + ";" + newPosition.getX();

                        send("flb", messageToSend, "localhost", portToSend);
                    }

                    //SeeToSend
                        case "ss" -> {
                            Position actuallPos = new Position(Integer.parseInt(splittedArray[2]), Integer.parseInt(splittedArray[3]));
                            List<String> tempCharList = new ArrayList<>();

                            map[actuallPos.getY()][actuallPos.getX()].setContent('P');
                            hostPanel.repaint();

                            for (int i = -1; i < 2; i++) {
                                for (int j = -1; j < 2; j++) {
                                    if ((actuallPos.getY() + i) >= 0 && (actuallPos.getY() + i) < 100 && (actuallPos.getX() + j) >= 0 && (actuallPos.getX() + j) < 150) {
                                        tempCharList.add(String.valueOf(map[actuallPos.getY() + i][actuallPos.getX() + j].getContent()));
                                        tempCharList.add(String.valueOf(map[actuallPos.getY() + i][actuallPos.getX() + j].getWaitTime()));
                                    } else {
                                        tempCharList.add(" ");
                                        tempCharList.add(" ");
                                    }
                                }
                            }

                            messageToSend = tempCharList.get(0) + ";" + tempCharList.get(1) + ";" + tempCharList.get(2) + ";" + tempCharList.get(3) + ";" + tempCharList.get(4) +
                                    ";" + tempCharList.get(5) + ";" + tempCharList.get(6) + ";" + tempCharList.get(7) + ";" + tempCharList.get(8) + ";" + tempCharList.get(9) +
                                    ";" + tempCharList.get(10) + ";" + tempCharList.get(11) + ";" + tempCharList.get(12) + ";" + tempCharList.get(13) + ";" + tempCharList.get(14) +
                                    ";" + tempCharList.get(15) + ";" + tempCharList.get(16) + ";" + tempCharList.get(17);

                            send("sb", messageToSend, "localhost", portToSend);
                        }


                        //MoveToSend
                        case "ms" -> {
                            Position actuallPos = new Position(Integer.parseInt(splittedArray[2]), Integer.parseInt(splittedArray[3]));

                            Position nextPossition = new Position(Integer.parseInt(splittedArray[4]), Integer.parseInt(splittedArray[5]));

                            if (map[nextPossition.getY()][nextPossition.getX()].getContent() == 'E') {
                                map[actuallPos.getY()][actuallPos.getX()].setContent('E');
                                map[actuallPos.getY()][actuallPos.getX()].setWaitTime(500);
                                map[nextPossition.getY()][nextPossition.getX()].setContent('P');


                                messageToSend = "T;" + nextPossition.getY() + ";" + nextPossition.getX();
                            } else {
                                messageToSend = "F";
                            }
                            hostPanel.repaint();

                            send("mb", messageToSend, "localhost", portToSend);
                        }


                        //TakeToSend
                        case "ts" -> {
                            Position tresurePossition = new Position(Integer.parseInt(splittedArray[4]), Integer.parseInt(splittedArray[5]));
                            if (map[tresurePossition.getY()][tresurePossition.getX()].getContent() == 'T') {
                                map[tresurePossition.getY()][tresurePossition.getX()].setContent('E');
                                //TODO punktacja
                                messageToSend = "T;" + tresurePossition.getY() + ";" + tresurePossition.getX();
                            } else {
                                messageToSend = "F";
                            }
                            hostPanel.repaint();
                            send("tb", messageToSend, "localhost", portToSend);
                        }
                    }


                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        operations.start();
    }

    private void emptyPlace(Position newPosition){
        if(!(map[newPosition.getY()][newPosition.getX()].getContent() == 'E')){
            newPosition.setY(new Random().nextInt(cords.getY()));
            newPosition.setX(new Random().nextInt(cords.getX()));

            emptyPlace(newPosition);
        }
    }

    private int createUniquePort(){
        int personalPort = new Random().nextInt(999);

        if(uniquePorts.contains(personalPort)){
            createUniquePort();
        }
        uniquePorts.add(personalPort);
        return personalPort;
    }
}
