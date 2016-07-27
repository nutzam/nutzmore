package org.nutz;

import java.io.File;
import java.util.List;

import org.nutz.lang.Files;
import org.nutz.lang.Strings;

public class AviMaker {

    public static void main(String[] args) {
        for (File f : new File("C:\\Users\\wendal\\workspace\\git\\github\\nutzmore").listFiles()) {
            if (!f.getName().startsWith("nutz-")) 
                continue;
            //System.out.println(f);
            File readme = new File(f, "README.md");
            if (!readme.exists()) {
                System.out.println("miss README.md "+ f);
                StringBuilder sb = new StringBuilder();
                sb.append("" + f.getName()).append("\r\n");
                sb.append("==================================\r\n\r\n");
                sb.append("简介(可用性:开发中)\r\n");
                sb.append("==================================\r\n\r\n");
                sb.append("待编写");
                Files.write(readme, sb);
            }
            boolean flag = true;
            List<String> lines = Files.readLines(readme);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith("简介(可用性:")) {
                    String avi = line.substring("简介(可用性:".length(), line.length()-1);
                    //System.out.println(lines.get(i+3));
                    String shortt = lines.get(i+3);
                    System.out.println("|"+f.getName()+"|"+shortt+"|"+avi+"|");
                    flag = false;
                    break;
                }
            }
            if (flag) {
                StringBuilder sb = new StringBuilder();
                sb.append("简介(可用性:开发中)\r\n");
                sb.append("==================================\r\n\r\n");
                sb.append("待编写");
                lines.add(3, sb.toString());
                Files.write(readme, Strings.join("\r\n", lines.toArray()));
            }
        }
    }

}
