public class token {
    private int position=0;
    private int line=1;
    private String value;

    public void incrementX(){
        position++;
    }

    public void incrementY(){
        line++;
    }

    public void setName(String value){
        this.value = value;
    }
    public void setPosition(int position){
        this.position=position;
    }

    public void setLine(int line){
        this.line=line;
    }
    public void resetPosition(){
        position=1;
    }
    public void resetLine(){
        line=1;
    }

   public void positionTab(){
        position+=3;
   }


    public String getValue(){
        return this.value;
    }

    public int getPosition(){
        return this.position;
    }

    public int getLine(){
        return this.line;
    }
}
