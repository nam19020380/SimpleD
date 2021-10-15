import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Dictionary {
    private ArrayList<Word> word_list;

    public Dictionary() {
        word_list = new ArrayList<Word>();
    }

    public void add(String word_target, String word_explain) {
        Word temp = new Word(word_target, word_explain);
        word_list.add(temp);
    }

    public int getSize() {
        return word_list.size();
    }

    public Word getWord(int i) {
        return word_list.get(i);
    }

    public void delete(String word_target) {
        for (int i = 0; i < word_list.size(); i++) {
            if (word_list.get(i).getWord_target().equalsIgnoreCase(word_target)) {
                word_list.remove(word_list.get(i));
            }
        }
    }

    public void in() throws IOException {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream("E:\\Dictext\\Dictdata.txt");
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

        Scanner scanner = new Scanner(fis);

        while (scanner.hasNextLine()) {
            Word temp = new Word(scanner.nextLine(), scanner.nextLine());
            word_list.add(temp);
        }

        fis.close();
    }

    public void out() throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("E:\\Dictext\\Dictdata.txt");
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        }

        Writer writer = new OutputStreamWriter(fos, "utf8");

        for (int i = 0; i < word_list.size(); ++i) {
            writer.write((String) word_list.get(i).getWord_target());
            writer.write("\r\n");
            writer.write((String) word_list.get(i).getWord_explain());
            writer.write("\r\n");
        }
        writer.flush();
        fos.close();
    }
}
