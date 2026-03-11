package com.habil.game.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Stockfish {
  private Process engine;
  private BufferedReader reader;
  private BufferedWriter writer;

   public void start() throws Exception{
    ProcessBuilder pb = new ProcessBuilder("stockfish(engine)/stockfish-windows-x86-64-avx2.exe");
    engine = pb.start();

    reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
    writer = new BufferedWriter(new OutputStreamWriter(engine.getOutputStream()));
  }

  public void sendCommand(String command)throws Exception{
    writer.write(command +"\n");
    writer.flush();
  }

  public String getOutput()throws Exception{
    String line;
    while((line = reader.readLine()) != null){
      if(line.startsWith("bestmove")){
        return line;
      }
    }
    return null;
  }
}
