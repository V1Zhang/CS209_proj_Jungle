package view.ChessComponent;


import controller.GameController;
import model.PlayerColor;
import view.ChessboardComponent;

import javax.swing.*;
import java.awt.*;

/**
 * This is the equivalent of the ChessPiece class,
 * but this class only cares how to draw Chess on ChessboardComponent
 */
public class AnimalChessComponent extends JComponent {
    private PlayerColor owner;

    private boolean selected = false;

    public AnimalChessComponent() {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected()) { // Highlights the model if selected.
            if (owner == PlayerColor.RED) {
                g.setColor(Color.RED);
            } else if (owner == PlayerColor.BLUE) {
                g.setColor(Color.BLUE);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(3.0f));
            g.drawRect(0, 0, getWidth(), getHeight());



        }
    }
    public boolean getIsSelect(){
        return selected;
    }
}
