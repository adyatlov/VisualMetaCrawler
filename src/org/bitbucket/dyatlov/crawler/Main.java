package org.bitbucket.dyatlov.crawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Simple Web Crawler");
        if (args.length < 3) {
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
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), "utf-8"));
        } catch (IOException ex) {
            System.out.println("Cannot create file: " + args[2]);
            System.exit(3);
            return;
        }

        Crawler crawler = new Crawler(url, max, new PageFetcher(), new PageParser());
        crawler.crawl();

        Collection<PageInfo> pages = crawler.getPages();

        // Write results to the file
        try {
            for (PageInfo page: pages) {
                writer.write(page.getPageTitle());
                writer.write(" , ");
                writer.write(page.getPageUrl().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error occurred when writing to the file: " + args[2]);
            System.exit(4);
            return;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error occurred when closing the file: " + args[2]);
                    System.exit(5);
                    return;
                }
            }
        }
        System.exit(0);
    }
}
