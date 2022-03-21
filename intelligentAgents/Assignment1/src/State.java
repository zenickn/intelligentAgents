public class State {
    
    public int row;
    public int col;
    public double reward;
    public double value;
    public String[] actions = {};
    public boolean iswall;

    public State(){}
    public State(int r , int c){
        iswall=false;
        row = r;
        col= c;
        
    }
    public State clone(){
        State state1 = new State(this.row,this.col);
        state1.setReward(this.reward);
        state1.setValue(this.value);
        state1.setActions(this.actions);
        state1.setIsWall(this.iswall);
        return state1;
    }

    public boolean getIsWall(){
        return iswall;
    }
        
    public void setIsWall(Boolean iswall) {
        this.iswall = iswall;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    public String[] getActions() {
        return actions;
    }
    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public double getReward() {
        return this.reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

   
    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

}
