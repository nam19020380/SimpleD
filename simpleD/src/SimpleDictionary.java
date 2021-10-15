import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import javax.speech.Central;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SimpleDictionary extends JFrame implements ActionListener {
    private JButton bt1;
    private JButton bt2;
    private JButton bt3;
    private JButton bt4;
    private JTextField tf1;
    private JTextField tf2;
    private String result;
    private Container cont = this.getContentPane();
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JComboBox cb1;
    private Dictionary dictionary = new Dictionary();

    public SimpleDictionary(String s) throws IOException {
        super(s);
        dictionary.in();
        JLabel eText = new JLabel("English ");
        this.tf1 = new JTextField();
        JLabel vText = new JLabel("Vietnamese ");
        this.tf2 = new JTextField();
        this.tf2.setEditable(false);
        this.panel1 = new JPanel();
        this.panel1.setLayout(new GridLayout(2, 2));
        this.panel1.add(eText);
        this.panel1.add(vText);
        this.panel1.add(this.tf1);
        this.panel1.add(this.tf2);
        this.bt1 = new JButton("Translate");
        this.bt2 = new JButton("Speak");
        this.bt3 = new JButton("Add");
        this.bt4 = new JButton("Change");
        this.panel2 = new JPanel();
        this.panel2.add(this.bt1);
        this.panel2.add(this.bt2);
        this.panel2.add(this.bt3);
        this.panel2.add(this.bt4);
        String[] mode = new String[]{"Online Mode", "Offline Mode"};
        this.cb1 = new JComboBox(mode);
        this.panel3 = new JPanel();
        this.panel3.add(this.cb1);
        this.cont.add(this.panel1);
        this.cont.add(this.panel2, "South");
        this.cont.add(this.panel3, "North");
        this.bt1.addActionListener(this);
        this.bt2.addActionListener(this);
        this.bt3.addActionListener(this);
        this.bt4.addActionListener(this);
        this.bt4.setEnabled(false);
        this.cb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (SimpleDictionary.this.cb1.getItemAt(SimpleDictionary.this.cb1.getSelectedIndex()) != "Online Mode") {
                    SimpleDictionary.this.bt3.setText("Delete");
                    SimpleDictionary.this.bt4.setEnabled(true);
                } else {
                    SimpleDictionary.this.bt3.setText("Add");
                    SimpleDictionary.this.bt4.setEnabled(false);
                }

            }
        });
        this.pack();
        this.setVisible(true);
    }

    private static String translate(String langFrom, String langTo, String text) throws IOException {
        String urlStr = "https://script.google.com/macros/s/AKfycby0S1zze-SXYcteidsz2o1OTykCY3YKUXWdp3kiEDsieUoB81vr/exec?q=" + URLEncoder.encode(text, "UTF-8") + "&target=" + langTo + "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        return response.toString();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Add" && this.cb1.getItemAt(this.cb1.getSelectedIndex()) == "Online Mode" && this.tf2.getText() != null) {
            this.dictionary.add(this.tf1.getText(), this.tf2.getText());
        }

        if (e.getActionCommand() == "Translate" && this.cb1.getItemAt(this.cb1.getSelectedIndex()) == "Online Mode") {
            try {
                this.result = translate("en", "vi", this.tf1.getText());
                this.tf2.setText(this.result);
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        if (e.getActionCommand() == "Translate" && this.cb1.getItemAt(this.cb1.getSelectedIndex()) != "Online Mode") {
            int x = 0;

            boolean y;
            for (y = false; x < this.dictionary.getSize(); ++x) {
                if (((String) this.dictionary.getWord(x).getWord_target()).equalsIgnoreCase(this.tf1.getText())) {
                    this.tf2.setText((String) this.dictionary.getWord(x).getWord_explain());
                    y = true;
                }
            }

            if (!y) {
                this.tf2.setText("Không rõ!");
            }
        }

        if (e.getActionCommand() == "Delete") {
            this.dictionary.delete(this.tf1.getText());
        }

        if (e.getActionCommand() == "Change" && this.tf1.getText() != null && this.tf2.getText() != null) {
            this.bt1.setEnabled(false);
            this.bt2.setEnabled(false);
            this.bt3.setEnabled(false);
            this.bt4.setText("Done");
            this.tf2.setEditable(true);
            this.cb1.setEnabled(false);
            this.tf1.setEditable(false);
        }

        if (e.getActionCommand() == "Done") {
            this.dictionary.delete(this.tf1.getText());
            this.dictionary.add(this.tf1.getText(), this.tf2.getText());
            this.bt1.setEnabled(true);
            this.bt2.setEnabled(true);
            this.bt3.setEnabled(true);
            this.bt4.setText("Change");
            this.tf2.setEditable(false);
            this.cb1.setEnabled(true);
            this.tf1.setEditable(true);
        }

        if (e.getActionCommand() == "Speak") {
            this.dospeak(this.tf1.getText());
        }
    }

    public void dospeak(String Text) {
        try {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            synthesizer.resume();
            synthesizer.speakPlainText(this.tf1.getText(), (SpeakableListener) null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void main(String[] arg) throws IOException {
        SimpleDictionary simpleDictionary = new SimpleDictionary("SimpleDictionary");
        simpleDictionary.setSize(375, 175);
        simpleDictionary.setLocationRelativeTo((Component) null);
        simpleDictionary.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    simpleDictionary.dictionary.out();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }
}
