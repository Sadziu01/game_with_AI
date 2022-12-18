package pwr.sadowski.variables;

public class OObject {
    char content;
    int waitTime;

    public char getContent() {
        return content;
    }

    public void setContent(char content) {
        this.content = content;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public OObject(char content, int waitTime){
        this.content = content;
        this.waitTime = waitTime;
    }
}
