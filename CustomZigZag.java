package study_examples;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import study_examples.CustomCoordinate;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.FontInfo;
import com.motivewave.platform.sdk.common.Inputs;
import com.motivewave.platform.sdk.common.Instrument;
import com.motivewave.platform.sdk.common.PathInfo;
import com.motivewave.platform.sdk.common.Util;
import com.motivewave.platform.sdk.common.desc.BooleanDescriptor;
import com.motivewave.platform.sdk.common.desc.DoubleDescriptor;
import com.motivewave.platform.sdk.common.desc.FontDescriptor;
import com.motivewave.platform.sdk.common.desc.InputDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.common.desc.SettingGroup;
import com.motivewave.platform.sdk.common.desc.SettingTab;
import com.motivewave.platform.sdk.common.desc.SettingsDescriptor;
import com.motivewave.platform.sdk.draw.Label;
import com.motivewave.platform.sdk.draw.Line;
import com.motivewave.platform.sdk.study.RuntimeDescriptor;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;

/** Zig Zag */
@StudyHeader(
    namespace="com.motivewave", 
    id="ZIG_ZAG", 
    rb="study_examples.nls.strings",
    name="TITLE_ZIG_ZAG", 
    desc="DESC_ZIG_ZAG",
    menu="Examples",
    overlay=true,
    studyOverlay=true,
    helpLink="http://www.motivewave.com/studies/zig_zag.htm")


public class CustomZigZag extends Study 
{
  final static String HIGH_INPUT = "highInput", LOW_INPUT = "lowInput", REVERSAL = "reversal";
  final static String PRICE_MOVEMENTS = "priceMovements", PRICE_LABELS = "priceLabels", RETRACE_LINE = "retraceLine";
  final static String SWING_NUMBER = "numOfSwings";
  
  final static String YEAR="Year", MONTH="Month", DAY="Day", HOUR="Hour", MINUTE="Minute", SECOND="Second";
  
  @Override
  public void initialize(Defaults defaults)
  {
    SettingsDescriptor sd = new SettingsDescriptor();
    SettingTab tab = new SettingTab(get("TAB_GENERAL"));
    sd.addTab(tab);
    setSettingsDescriptor(sd);
    
    SettingGroup inputs = new SettingGroup(get("LBL_INPUTS"));
    
    inputs.addRow(new InputDescriptor(HIGH_INPUT, get("LBL_HIGH_INPUT"), Enums.BarInput.MIDPOINT));
    inputs.addRow(new InputDescriptor(LOW_INPUT, get("LBL_LOW_INPUT"), Enums.BarInput.MIDPOINT));
    inputs.addRow(new DoubleDescriptor(REVERSAL, get("LBL_REVERSAL"), 1.0, 0.0001, 99.999, 0.0001));
    inputs.addRow(new BooleanDescriptor(PRICE_MOVEMENTS, get("LBL_PRICE_MOVEMENTS"), true));
    inputs.addRow(new BooleanDescriptor(PRICE_LABELS, get("LBL_PRICE_LABELS"), true));
    inputs.addRow(new BooleanDescriptor(SWING_NUMBER, get("LBL_SWING_NUMBER"), true));
    inputs.addRow(new FontDescriptor(Inputs.FONT, get("LBL_FONT"), defaults.getFont()));
    inputs.addRow(new IntegerDescriptor(YEAR, get("STARTING_YEAR"), 2012, 1980, 2100, 1));
    inputs.addRow(new IntegerDescriptor(MONTH, get("STARTING_MONTH"), 9, 1, 12, 1));
    inputs.addRow(new IntegerDescriptor(DAY, get("STARTING_DAY"), 10, 1, 31, 1));
    inputs.addRow(new IntegerDescriptor(HOUR, get("STARTING_HOUR"), 10, 0, 24, 1));
    inputs.addRow(new IntegerDescriptor(MINUTE, get("STARTING_MINUTE"), 0, 0, 60, 1));
    inputs.addRow(new IntegerDescriptor(SECOND, get("STARTING_SECOND"), 0, 0, 60, 1));
    tab.addGroup(inputs);
    
    SettingGroup colors = new SettingGroup(get("LBL_COLORS"));
    colors.addRow(new PathDescriptor(Inputs.PATH, get("LBL_LINE"), defaults.getLineColor(), 1.0f, null, true, false, false));
    colors.addRow(new PathDescriptor(RETRACE_LINE, get("LBL_RETRACE_LINE"), defaults.getLineColor(), 1.0f, new float[] {3f, 3f}, true, false, true));
    tab.addGroup(colors);
    
    RuntimeDescriptor desc = new RuntimeDescriptor();
    desc.setLabelSettings(HIGH_INPUT, LOW_INPUT, REVERSAL);
    setRuntimeDescriptor(desc);
    setMinBars(200);
  }
  
  @SuppressWarnings("null")
  @Override
  protected void calculateValues(DataContext ctx)
  {
	int reversalCount = 0;
    Object highInput = getSettings().getInput(HIGH_INPUT);
    Object lowInput = getSettings().getInput(LOW_INPUT);
    double reversal = getSettings().getDouble(REVERSAL, 1.0)/100.0;
    boolean movements = getSettings().getBoolean(PRICE_MOVEMENTS, true);
    boolean priceLabels = getSettings().getBoolean(PRICE_LABELS, true);
    boolean numOfSwings = getSettings().getBoolean(SWING_NUMBER, true);
    PathInfo line = getSettings().getPath(Inputs.PATH);
    PathInfo retraceLine = getSettings().getPath(RETRACE_LINE);
    Defaults defaults = ctx.getDefaults();
    FontInfo fi = getSettings().getFont(Inputs.FONT);
    Font f = fi == null ? defaults.getFont() : fi.getFont();
    Color bgColor = defaults.getBackgroundColor();
    Color txtColor = line.getColor();
    
    DataSeries series = ctx.getDataSeries();
    Instrument instr = ctx.getInstrument();
    clearFigures();

    // Find a local low or high
    double max = series.getDouble(0, highInput);
    double min = series.getDouble(0, lowInput);

    int minBar = 0;
    int maxBar = 0;
    boolean up = false;

    // Determine the initial direction
    double val = series.getDouble(1, highInput);
    if (val > max) up = true;
    else up = false;
    
    List<CustomCoordinate> points = new ArrayList();
    for(int i = 0; i < series.size()-1; i++) {
      if (up) val = series.getDouble(i, highInput);
      else val = series.getDouble(i, lowInput);

      if (val>max) {
        max = val;
        maxBar = i;
        if (up) {
          min = max;
          minBar = maxBar;
        }
      }
      if (val<min) {
        min = val;
        minBar = i;
        
        if (!up) {
          max = min;
          maxBar = minBar;
        }
      }
      
      // Check to see if we have found a reversal point
      if (up && (min < (1.0-reversal)*max)) {
    	reversalCount++;
        points.add(new CustomCoordinate(series.getStartTime(maxBar), series.getHigh(maxBar),reversalCount));
        max = min;
        maxBar = minBar;
        up=false;
      }
      
      if (!up && (max > (1.0+reversal)*min)) {
    	reversalCount++;
        points.add(new CustomCoordinate(series.getStartTime(minBar), series.getLow(minBar),reversalCount));
        
        min = max;
        minBar = maxBar;
        up=true;
      }
    }
    
    if (up) {
      reversalCount++;
      points.add(new CustomCoordinate(series.getStartTime(maxBar), series.getHigh(maxBar),reversalCount));
    }
    else {
      reversalCount++;
      points.add(new CustomCoordinate(series.getStartTime(minBar), series.getLow(minBar),reversalCount));
    }
    
    // Build the ZigZag lines
    CustomCoordinate prev = null;
    CustomCoordinate prev2 = null;
    for(CustomCoordinate c : points) {

      // Zig Zag Lines
      if (prev != null) {
        Line l = new Line(prev, c, line);
        addFigure(l);
        if (movements) {
          l.setText(instr.format(Math.abs(c.getValue() - prev.getValue())), f);
          l.getText().setBackground(bgColor);
        }
      }
      
      // Retracements
      if (retraceLine != null && retraceLine.isEnabled() && prev2 != null) {
        Line l = new Line(prev2, c, retraceLine);
        double l1 = Math.abs(c.getValue() - prev.getValue());
        double l2 = Math.abs(prev2.getValue() - prev.getValue());
        double rt = l1/l2;
        l.setText(Util.round(rt*100, 1)+"%", f);
        addFigure(l);
      }
      prev2 = prev;
      prev = c;
    }
    
    // Price Labels
    if (priceLabels) {
      for(CustomCoordinate c : points) {
        Label lbl = new Label(instr.format(c.getValue()), f, txtColor, bgColor);
        lbl.setLocation(c);
        addFigure(lbl);
      }
    }
  
    if (numOfSwings) {
    	for(CustomCoordinate c : points) {
    		Label lbl = new Label (instr.format(c.getCount()), f, txtColor, bgColor);
    		lbl.setLocation(c);
    		addFigure(lbl);
    	}
    }
  }
}
