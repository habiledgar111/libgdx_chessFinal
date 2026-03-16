package com.habil.game.DTO;


public class BestMoveDTO {
  private boolean error;
  private String errorMessage;
  private String move;
  public boolean isError() {
    return error;
  }
  public void setError(boolean error) {
    this.error = error;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  public String getMove() {
    return move;
  }
  public void setMove(String move) {
    this.move = move;
  }
}
