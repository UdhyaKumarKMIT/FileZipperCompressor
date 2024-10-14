import javax.swing.*;

public class ThreadedText implements Runnable {
    private final String Text;
    private final JLabel F;
    Thread t;

    ThreadedText(String temp,JLabel t1)
    {
        this.Text=temp;
        this.F=t1;
    }
    public void run(){
        synchronized (this){
            String S1=new String();
            for(int i=0;i<Text.length();i++)
            {
                try {
                    S1+=Text.charAt(i) + "";
                    t.sleep(100);
                    F.setText(S1);
                }catch (InterruptedException e){}
            }
        }
    }
}

