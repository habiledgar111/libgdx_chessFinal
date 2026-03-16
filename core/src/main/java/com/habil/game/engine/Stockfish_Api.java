package com.habil.game.engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.habil.game.DTO.BestMoveDTO;

public class Stockfish_Api {

  private static final String API_URL = "https://chess-api.com/v1";
  private final Gson gson = new Gson();

  /**
   * Kirim FEN ke API dan mendapatkan best move
   */
  public CompletableFuture<BestMoveDTO> fetchBestMove(String fen, int depth) {

    String normalizedFen = normalizeFen(fen);

    return CompletableFuture.supplyAsync(() -> {

      BestMoveDTO result = new BestMoveDTO();

      try {

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        String json = String.format(
            "{\"fen\":\"%s\",\"depth\":%d}",
            normalizedFen,
            depth);

        try (OutputStream os = conn.getOutputStream()) {
          os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();

        if (status != 200) {
          result.setError(true);
          result.setErrorMessage("HTTP ERROR: " + status);
          return result;
        }

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

          StringBuilder response = new StringBuilder();
          String line;

          while ((line = reader.readLine()) != null) {
            response.append(line);
          }

          BestMoveDTO dto = gson.fromJson(response.toString(), BestMoveDTO.class);

          if (dto == null) {
            result.setError(true);
            result.setErrorMessage("Invalid JSON response");
            return result;
          }

          dto.setError(false);
          return dto;
        }

      } catch (Exception e) {

        result.setError(true);
        result.setErrorMessage(e.getMessage());
        return result;

      }

    });
  }

  private String normalizeFen(String fen) {

    String[] parts = fen.split(" ");

    if (parts.length >= 4) {
      parts[3] = "-";
    }

    return String.join(" ", parts);
  }
}