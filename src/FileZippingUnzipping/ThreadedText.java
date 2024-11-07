package FileZippingUnzipping;
import javax.swing.*;

    public class ThreadedText implements Runnable {
        private final String Text;
        private final JLabel F;
        Thread t;

        ThreadedText(String temp, JLabel t1) {
            this.Text = temp;
            this.F = t1;
            this.t = new Thread(this);
        }

        public void run() {
            String S1 = new String();
            for (int i = 0; i < Text.length(); i++) {
                try {
                    S1 += Text.charAt(i);
                    Thread.sleep(100);
                    final String tempS1 = S1;

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            F.setText(tempS1);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
