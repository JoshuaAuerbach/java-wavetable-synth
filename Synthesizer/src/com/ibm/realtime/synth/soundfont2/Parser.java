/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
// This source file is a stub that needs to be replaced with functioning code.

package com.ibm.realtime.synth.soundfont2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Parser {
  public static boolean TRACE = false;
  public static boolean TRACE_INFO = false;
  public static boolean TRACE_RIFF = false;
  public static boolean TRACE_RIFF_MORE = false;
  public static boolean TRACE_PRESET = false;
  public static boolean TRACE_PROCESSOR = false;
  public static boolean TRACE_GENERATORS = false;
  public static boolean TRACE_MODULATORS = false;
  public static boolean TRACE_SAMPLELINKS = false;
  public SoundFontInfo getInfo() {
	  return null;
  }
  public List<SoundFontBank> getPresetBanks() {
	  return null; 
  }
  public SoundFontSampleData getSampleData() {
	  return null;
  }
  public void load(InputStream in) throws IOException,
          SoundFont2ParserException {
  }
  public static class SoundFont2ParserException extends Exception {
      public static final long serialVersionUID = 0;
      public SoundFont2ParserException(String msg) {
          super(msg);
      }
  }
}
