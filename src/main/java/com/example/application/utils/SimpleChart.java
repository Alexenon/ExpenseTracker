package com.example.application.utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimpleChart extends JPanel {
    private final List<Double> prices;

    public SimpleChart(List<Double> prices) {
        this.prices = prices;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        // Chart padding
        int padding = 50;
        int labelPadding = 30;

        double min = prices.stream().min(Double::compareTo).orElse(0.0);
        double max = prices.stream().max(Double::compareTo).orElse(0.0);

        int n = prices.size();
        double xScale = ((double) (w - 2 * padding - labelPadding)) / (n - 1);
        double yScale = ((double) (h - 2 * padding - labelPadding)) / (max - min);

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.BLACK);

        // Draw axes
        g2.drawLine(padding + labelPadding, h - padding, w - padding, h - padding); // X axis
        g2.drawLine(padding + labelPadding, h - padding, padding + labelPadding, padding); // Y axis

        // Draw line
        int prevX = (int) (padding + labelPadding + 0 * xScale);
        int prevY = (int) (h - padding - (prices.getFirst() - min) * yScale);

        g2.setColor(Color.BLUE);
        for (int i = 1; i < n; i++) {
            int x = (int) (padding + labelPadding + i * xScale);
            int y = (int) (h - padding - (prices.get(i) - min) * yScale);
            g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        g2.setColor(Color.BLACK);

        // Draw Y axis labels (prices)
        int divisions = 10;
        for (int i = 0; i <= divisions; i++) {
            double yValue = min + (max - min) * i / divisions;
            int y = (int) (h - padding - (yValue - min) * yScale);
            g2.drawLine(padding + labelPadding - 5, y, padding + labelPadding, y);
            g2.drawString(String.format("%.2f", yValue), 5, y + 5);
        }

        // Draw X axis labels (days)
        for (int i = 0; i < n; i += Math.max(1, n / 10)) {
            int x = (int) (padding + labelPadding + i * xScale);
            g2.drawLine(x, h - padding, x, h - padding + 5);
            g2.drawString(String.valueOf(i + 1), x - 5, h - padding + 20);
        }

        // Axis titles
        g2.drawString("Days", w / 2, h - 10);
        g2.drawString("Price", 10, h / 2);
    }

    public static void showChart(List<Double> prices) {
        JFrame frame = new JFrame("MarketSimulator Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new SimpleChart(prices));
        frame.setVisible(true);
    }
}