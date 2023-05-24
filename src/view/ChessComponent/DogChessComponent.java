package view.ChessComponent;

import model.PlayerColor;
import view.ChessComponent.AnimalChessComponent;

import java.awt.*;

/**
 * This is the equivalent of the ChessPiece class,
 * but this class only cares how to draw Chess on ChessboardComponent
 */
public class DogChessComponent extends AnimalChessComponent {
    private PlayerColor owner;

    private boolean selected;

    public DogChessComponent(PlayerColor owner, int size) {
        this.owner = owner;
        this.selected = false;
        setSize(size/2, size/2);
        setLocation(0,0);
        setVisible(true);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private void drawImageR(Graphics g){
        Graphics2D gR8 = (Graphics2D) g.create();
        Image imageR = Toolkit.getDefaultToolkit().getImage("src/images/红狗.jpg");
        gR8.drawImage(imageR,0,0,getWidth() , getHeight(),this);
        gR8.dispose();
    }
    private void drawImageB(Graphics g){
        Graphics2D gB8 = (Graphics2D) g.create();
        Image imageB = Toolkit.getDefaultToolkit().getImage("src/images/蓝狗.jpg");
        gB8.drawImage(imageB,0,0,getWidth() , getHeight(),this);
        gB8.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(owner==PlayerColor.RED){
            drawImageR(g);
        } else if (owner==PlayerColor.BLUE){
            drawImageB(g);
        }
        if (isSelected()) { // Highlights the model if selected.
            if(owner==PlayerColor.RED) {
                g.setColor(Color.RED);
            }
            else if (owner==PlayerColor.BLUE){
                g.setColor(Color.BLUE);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(3.0f));
            g.drawRect(0, 0, getWidth() , getHeight());
        }
    }
}
