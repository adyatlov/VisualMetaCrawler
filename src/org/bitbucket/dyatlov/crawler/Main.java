package org.bitbucket.dyatlov.crawler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Simple Web Crawler");
            System.out.println("Usage:");
            System.out.println("First argument: page URL e.g. http://example.com");
            System.out.println("Second argument: number of pages to obtain e.g. 1000");
            System.out.println("Third argument: output file name, e.g links.csv");
            System.exit(-1);
        }
        URL url;
        try {
            url = new URL(args[0]);
        } catch (MalformedURLException e) {
            System.out.println("Page URL " + args[0] + " is malformed");
            System.exit(1);
            return;
        }

        int max;
        try {
            max = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Number of pages argument is not a number: " + args[1]);
            System.exit(2);
            return;
        }

        // Create file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), "utf-8"));
        } catch (IOException ex) {
            System.out.println("Cannot create file: " + args[2]);
            System.exit(3);
            return;
        }

        // Crawl!
        Crawler crawler = new Crawler(url, max, new PageFetcher(), new PageParser());
        crawler.crawl();

        // Write results to the file
        try {
            for (String link: crawler.getLinks()) {
                writer.write(link);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error occurred when writing to the file: " + args[2]);
            System.exit(4);
            return;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
