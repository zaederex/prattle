package com.neu.prattle.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class FileHandler {
  private static Logger logger = LoggerFactory.getLogger(FileHandler.class);

  private FileHandler() {
    // private constructor to hide default constructor
  }

  /**
   * Writes the media type content to a file and returns the file path to be saved in the
   * attachments table.
   *
   * @param data the data to create the file for.
   * @return the path of the file created.
   */
  public static String createFileForMediaTypeData(String data) {
    String filename = UUID.randomUUID().toString() + ".txt";
    try (
            FileWriter fileWriter = new FileWriter(filename, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
    ) {
      writer.write(data);
      writer.flush();
      return filename;
    } catch (IOException e) {
      logger.error("Unable to write to file.");
    }
    return "Error";
  }

  /**
   * Gets the file data from a file.
   *
   * @param filepath the path of the file.
   * @return the original file data.
   */
  public static String getOriginalMediaTypeData(String filepath) {
    StringBuilder data = new StringBuilder();

    if (filepath == null || filepath.isEmpty()) {
      return data.toString();
    }

    try (
            FileReader fileReader = new FileReader(filepath);
            BufferedReader reader = new BufferedReader(fileReader);
    ) {
      while (reader.ready()) {
        data.append(reader.readLine());
      }
      return data.toString();
    } catch (IOException e) {
      logger.error("Unable to read file.");
    }
    return "Error";
  }
}
