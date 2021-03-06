/*
 * Copyright 2010 Srikanth Reddy Lingala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lingala.zip4j.util;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.File;
import java.util.Calendar;

public class Zip4jUtil {

  public static boolean isStringNotNullAndNotEmpty(String str) {
    return str != null && str.trim().length() > 0;
  }

  public static boolean createDirectoryIfNotExists(File file) throws ZipException {
    if (file == null) {
      throw new ZipException("output path is null");
    }

    if (file.exists()) {
      if (!file.isDirectory()) {
        throw new ZipException("output directory is not valid");
      }
    } else {
      if (!file.mkdirs()) {
        throw new ZipException("Cannot create output directories");
      }
    }

    return true;
  }

  public static long javaToDosTime(long time) {

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(time);

    int year = cal.get(Calendar.YEAR);
    if (year < 1980) {
      return (1 << 21) | (1 << 16);
    }
    return (year - 1980) << 25 | (cal.get(Calendar.MONTH) + 1) << 21 |
        cal.get(Calendar.DATE) << 16 | cal.get(Calendar.HOUR_OF_DAY) << 11 | cal.get(Calendar.MINUTE) << 5 |
        cal.get(Calendar.SECOND) >> 1;
  }

  public static long dosToJavaTme(long dosTime) {
    int sec = (int) (2 * (dosTime & 0x1f));
    int min = (int) ((dosTime >> 5) & 0x3f);
    int hrs = (int) ((dosTime >> 11) & 0x1f);
    int day = (int) ((dosTime >> 16) & 0x1f);
    int mon = (int) (((dosTime >> 21) & 0xf) - 1);
    int year = (int) (((dosTime >> 25) & 0x7f) + 1980);

    Calendar cal = Calendar.getInstance();
    cal.set(year, mon, day, hrs, min, sec);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime().getTime();
  }

  public static byte[] convertCharArrayToByteArray(char[] charArray) {
    byte[] bytes = new byte[charArray.length];
    for (int i = 0; i < charArray.length; i++) {
      bytes[i] = (byte) charArray[i];
    }
    return bytes;
  }

  public static CompressionMethod getCompressionMethod(LocalFileHeader localFileHeader) {
    if (localFileHeader.getCompressionMethod() != CompressionMethod.AES_INTERNAL_ONLY) {
      return localFileHeader.getCompressionMethod();
    }

    if (localFileHeader.getAesExtraDataRecord() == null) {
      throw new RuntimeException("AesExtraDataRecord not present in local header for aes encrypted data");
    }

    return localFileHeader.getAesExtraDataRecord().getCompressionMethod();
  }

}
