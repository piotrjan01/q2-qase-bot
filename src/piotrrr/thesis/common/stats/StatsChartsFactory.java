/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piotrrr.thesis.common.stats;

import java.util.LinkedList;
import java.util.TreeMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import piotrrr.thesis.common.stats.BotStatistic.Kill;

/**
 *
 * @author piotrrr
 */
public class StatsChartsFactory {

    private static class BotSeries {
        XYSeries series;
        int int1;
        int int2;
        String botName;

        public BotSeries(XYSeries s, int i1, int i2, String botName) {
            this.series = s;
            this.int1 = i1;
            this.int2 = i2;
            this.botName = botName;
        }

        
    }

    public static ChartPanel getKillsInTimeByBotType(BotStatistic stats) {
        XYSeriesCollection ds = new XYSeriesCollection();

        LinkedList<BotSeries> series = new LinkedList<BotSeries>();

        for (String botName : stats.getAllBotFamilies()) {
            series.add(new BotSeries(new XYSeries(botName), 0, 0, botName));
        }

        for (BotSeries s : series) {
            s.series.add(0, 0);
        }

        for (Kill k : stats.kills) {
            for (BotSeries s : series) {
                if (k.killer.startsWith(s.botName)) s.int1++;
                s.series.add(k.time/10, s.int1);
            }
        }

        for (BotSeries s : series) ds.addSeries(s.series);

         JFreeChart c = ChartFactory.createXYLineChart(
                 "Kills by bot type in time",
                 "time [s]",
                 "kills",
                 ds,
                 PlotOrientation.VERTICAL,
                 true, true, true);

        ChartPanel cp = new ChartPanel(c);
        return cp;

    }

    public static ChartPanel getDeathsInTimeByBotType(BotStatistic stats) {
        XYSeriesCollection ds = new XYSeriesCollection();

        LinkedList<BotSeries> series = new LinkedList<BotSeries>();

        for (String botName : stats.getAllBotFamilies()) {
            series.add(new BotSeries(new XYSeries(botName), 0, 0, botName));
        }

        for (BotSeries s : series) {
            s.series.add(0, 0);
        }

        for (Kill k : stats.kills) {
            for (BotSeries s : series) {
                if (k.victim.startsWith(s.botName)) s.int1++;
                s.series.add(k.time/10, s.int1);
            }
        }

        for (BotSeries s : series) ds.addSeries(s.series);

         JFreeChart c = ChartFactory.createXYLineChart(
                 "Deaths by bot type in time",
                 "time [s]",
                 "deaths",
                 ds,
                 PlotOrientation.VERTICAL,
                 true, true, true);

        ChartPanel cp = new ChartPanel(c);
        return cp;

    }

     public static ChartPanel getKillsPerEachDeathByBotType(BotStatistic stats) {
        XYSeriesCollection ds = new XYSeriesCollection();

        LinkedList<BotSeries> series = new LinkedList<BotSeries>();

        for (String botName : stats.getAllBotFamilies()) {
            series.add(new BotSeries(new XYSeries(botName), 0, 0, botName));
        }

        for (BotSeries s : series) {
            s.series.add(0, 0);
        }

        for (Kill k : stats.kills) {
            for (BotSeries s : series) {
                if (k.killer.startsWith(s.botName)) s.int1++;
                if (k.victim.startsWith(s.botName)) s.int2++;
                float val = 0;
                if (s.int2 != 0) val = (float)s.int1 / (float)s.int2;
                s.series.add(k.time/10, val);
            }
        }

        for (BotSeries s : series) ds.addSeries(s.series);

         JFreeChart c = ChartFactory.createXYLineChart(
                 "Kills per each death by bot type in time",
                 "time [s]",
                 "kills / deaths",
                 ds,
                 PlotOrientation.VERTICAL,
                 true, true, true);

        ChartPanel cp = new ChartPanel(c);
        return cp;

    }

    public static ChartPanel getKillsInTimeByBot(BotStatistic stats) {
        
        XYSeriesCollection ds = new XYSeriesCollection();
        
        LinkedList<BotSeries> series = new LinkedList<BotSeries>();

        for (String botName : stats.getAllBotsNames()) {
            series.add(new BotSeries(new XYSeries(botName), 0, 0, botName));
        }

        for (BotSeries s : series) {
            s.series.add(0, 0);
        }

        for (Kill k : stats.kills) {
            for (BotSeries s : series) {
                if (s.botName.equals(k.killer)) s.int1++;
                s.series.add(k.time/10, s.int1);
            }
        }

        for (BotSeries s : series) ds.addSeries(s.series);

         JFreeChart c = ChartFactory.createXYLineChart(
                 "Kills by each bot in time",
                 "time [s]",
                 "kills",
                 ds,
                 PlotOrientation.VERTICAL,
                 true, true, true);

//        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) ((XYPlot)c.getPlot()).getRenderer();
//        r.setDrawOutlines(true);
//        r.setShapesVisible(true);

        ChartPanel cp = new ChartPanel(c);
        return cp;

    }

    private static class WeaponUsage {
        int blaster = 0;
        int sshotgun = 0;
        int shotgun = 0;
        int machinegun = 0;
        int railgun = 0;
        int hyperblaster = 0;
        int chaingun = 0;
        int rocket = 0;
        int other = 0;
        String name = "no-name";

    }

    public static ChartPanel getWeaponUseageByBotBarChart(BotStatistic stat) {

        TreeMap<String, TreeMap<String, Integer>> map = new TreeMap<String, TreeMap<String, Integer>>();

        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        for (String p : stat.getAllBotsNames()) map.put(p, new TreeMap<String, Integer>());

        for (Kill k : stat.kills) {
            TreeMap<String, Integer> usage = map.get(k.killer);
            if (usage.containsKey(k.gunUsed)) {
                int c = usage.get(k.gunUsed);
                usage.remove(k.gunUsed);
                usage.put(k.gunUsed, c+1);
            }
            else usage.put(k.gunUsed, 1);
        }

        for (String bn : map.keySet()) {
            TreeMap<String, Integer> usage = map.get(bn);
            for (String wpn : usage.keySet()) {
                ds.addValue((Number)usage.get(wpn), wpn, bn);
            }
        }
        
        JFreeChart c = ChartFactory.createStackedBarChart(
                "Weapon use by each bot",
                "Bot",
                "Weapon usage",
                ds,
                PlotOrientation.VERTICAL,
                true, true, true);



        ChartPanel cp = new ChartPanel(c);
        return cp;
    }

     public static ChartPanel getWhoKillsWhomBarChart(BotStatistic stat) {

        TreeMap<String, TreeMap<String, Integer>> map = new TreeMap<String, TreeMap<String, Integer>>();

        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        for (String p : stat.getAllBotsNames()) {
            TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
            for (String bn : stat.getAllBotsNames()) {
                tm.put(bn, 0);
            }
            map.put(p, tm);
        }

        for (Kill k : stat.kills) {
            TreeMap<String, Integer> tm = map.get(k.killer);
            int c = tm.get(k.victim);
            tm.remove(k.victim);
            tm.put(k.victim, c+1);
        }

        for (String bn : map.keySet()) {
            TreeMap<String, Integer> usage = map.get(bn);
            for (String wpn : usage.keySet()) {
                ds.addValue((Number)usage.get(wpn), wpn, bn);
            }
        }

        JFreeChart c = ChartFactory.createStackedBarChart(
                "Who how often killed whom",
                "Killer bot",
                "Kills by bot",
                ds,
                PlotOrientation.VERTICAL,
                true, true, true);



        ChartPanel cp = new ChartPanel(c);
        return cp;
    }



}
