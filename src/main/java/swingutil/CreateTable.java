package main.java.swingutil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class CreateTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int iden=0;
	private  String[][] rowData;
	private  String[] colData;
	private  TabelModel model;
	private JTable table;
	private int width,height,tmp,id,widthID;
	private double freeMem;
	
	public CreateTable(String[][] rowData, String[] colData){
		this.rowData =rowData;
		this.colData=colData;
		this.id=iden++;
		createModel();
		setDimensions();
		setTable();
		
	}

	private void setDimensions() {
		for(String widest: colData){			
			tmp = widest.length();
			if(tmp>width){
				this.width=tmp;				
			}
		}
				
		tmp=0;
		for(String[] highest: rowData){
			tmp++;				
		}
		this.height=tmp;
		
	}

	private void createModel(){
		
		this.model = new TabelModel(rowData, colData);
		
	}
	
	private void setTable(){
		
		table = new JTable(model)
		{			
			private static final long serialVersionUID = 1L;
			
			// selected row will be outlined
			private Border outside = new MatteBorder(1, 0, 1, 0, Color.BLACK);
			private Border inside = new EmptyBorder(0, 1, 0, 1);
			private Border highlight = new CompoundBorder(outside, inside);
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				
				
				
				
				Component c = super.prepareRenderer(renderer, row, column);
				//  Alternate row color				
				c.setBackground(row % 2 == 0 ? getBackground() : Color.LIGHT_GRAY);
				
				// check to see if memory warning color needs to be changed
				//System.out.println(freeMem);
				try {
					freeMem = ((Double.parseDouble((String)model.getValueAt(0, 2))/Double.parseDouble((String)model.getValueAt(0, 0)))*100);
					if((row == 0 && column == 2) && id ==0){
						if(freeMem<5)		c.setBackground(Color.RED);
						else if(freeMem<10)	c.setBackground(Color.ORANGE);
						else if(freeMem<15)	c.setBackground(Color.YELLOW);
						else				c.setBackground(Color.GREEN);
						
					}
				} catch(Exception e) {
					
				}
				// selected row will be outlined
				JComponent jc = (JComponent)c;
				if (isRowSelected(row))	jc.setBorder( highlight );
				
				return c;
			}			
		};
		
				
		table.getTableHeader().setBackground(Color.DARK_GRAY);
		table.getTableHeader().setForeground(Color.WHITE);
		table.setFillsViewportHeight(true);
        table.setLayout(new BorderLayout());
        table.setPreferredScrollableViewportSize(new Dimension(width*72,height*16));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for(int i=0; i < table.getColumnCount();i++){
        	
        	table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }        
	}
	
	public JTable getTable(){
		return table;
	}
	
}
