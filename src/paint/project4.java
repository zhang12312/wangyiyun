package paint;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;

public class project4 extends JFrame implements ActionListener {
	private	Container c = getContentPane();
	private	String menuBar[]={"文件(F)","编辑(E)","视图(V)","说明(H)"};
	private	String menuItem[][]={
		{"新建(N)|78","打开(O)|79","保存(S)|83","另存为(A)","退出(X)|88"},
		{"撤消(U)|90","重复(R)|89","剪切(T)|87","复制(C)|68","粘贴(P)|85"},
		{"工具箱(T)|84","色块(C)|76","状态栏(S)","属性栏(M)"},
		
	};
	private	JMenuItem jMenuItem[][]=new JMenuItem[4][5];
	private	JMenu jMenu[];
	private	JCheckBoxMenuItem jCheckBoxMenuItem[] = new JCheckBoxMenuItem[4];
	private	String ButtonName[]={"直线","矩开","椭圆","圆角矩形","贝氏曲线","扇型","多边形","铅笔","橡皮擦","文字","选取"};
	private JToggleButton jToggleButton[];
    private ButtonGroup buttonGroup;
	private	JPanel jPanel[]=new JPanel[5];//0內层,1绘图区,2工具箱,3色塊,4属性栏
	private	JLabel jLabel[]=new JLabel[1];//状态列
	private	String toolname[]={"src/img/tool1.gif","src/img/tool2.gif","src/img/tool3.gif","src/img/tool4.gif","src/img/tool5.gif","src/img/tool8.gif","src/img/tool9.gif","src/img/tool7.gif","src/img/tool6.gif","src/img/tool10.gif","src/img/tool11.gif"};
	private	Icon tool[]=new ImageIcon[11];
	private	int i,j,show_x,show_y,drawMethod=7,draw_panel_width=700,draw_panel_height=500;
	private Paint color_border,color_inside;
	private SetPanel setPanel;
	private DrawPanel drawPanel;
	private UnderDrawPanel underDrawPanel;
	private ColorPanel colorPanel;
	private Stroke stroke;
	private Shape shape;
	private String isFilled;
	
	//Download:http://www.codefans.net
	public project4(){
		setAlwaysOnTop(true);
		
		JMenuBar bar = new JMenuBar();
		jMenu=new JMenu[menuBar.length];
		for(i=0;i<menuBar.length;i++){
			jMenu[i] = new JMenu(menuBar[i]);
			jMenu[i].setMnemonic(menuBar[i].split("\\(")[1].charAt(0));
			bar.add(jMenu[i]);
		}
		
		for(i=0;i<menuItem.length;i++){
			for(j=0;j<menuItem[i].length;j++){
				if(i==0 && j==4 || i==1 && j==2) jMenu[i].addSeparator();
				if(i!=2){
					jMenuItem[i][j] = new JMenuItem(menuItem[i][j].split("\\|")[0]);
					if(menuItem[i][j].split("\\|").length!=1)
						jMenuItem[i][j].setAccelerator(KeyStroke.getKeyStroke(Integer.parseInt(menuItem[i][j].split("\\|")[1]), ActionEvent.CTRL_MASK) );
					jMenuItem[i][j].addActionListener(this);
					jMenuItem[i][j].setMnemonic(menuItem[i][j].split("\\(")[1].charAt(0));

					jMenu[i].add(jMenuItem[i][j]);
				}
				else{
					jCheckBoxMenuItem[j] = new JCheckBoxMenuItem(menuItem[i][j].split("\\|")[0]);
					if(menuItem[i][j].split("\\|").length!=1)
						jCheckBoxMenuItem[j].setAccelerator(KeyStroke.getKeyStroke(Integer.parseInt(menuItem[i][j].split("\\|")[1]), ActionEvent.CTRL_MASK) );
					jCheckBoxMenuItem[j].addActionListener(this);
					jCheckBoxMenuItem[j].setMnemonic(menuItem[i][j].split("\\(")[1].charAt(0));
					jCheckBoxMenuItem[j].setSelected( true );
					jMenu[i].add(jCheckBoxMenuItem[j]);
				}
			}
		}
		this.setJMenuBar( bar );
		c.setLayout( new BorderLayout() );
		for(i=0;i<5;i++)
			jPanel[i]=new JPanel();
			
		jLabel[0]=new JLabel(" 状态列");
		
		buttonGroup = new ButtonGroup();
		JToolBar jToolBar=new JToolBar("工具箱",JToolBar.VERTICAL);
		jToggleButton=new JToggleButton[ButtonName.length];
		for(i=0;i<ButtonName.length;i++){
			tool[i] = new ImageIcon(toolname[i]);
			jToggleButton[i] = new JToggleButton(tool[i]);
			jToggleButton[i].addActionListener( this );
			jToggleButton[i].setFocusable( false );
			buttonGroup.add(jToggleButton[i]);
		}
		jToolBar.add(jToggleButton[7]);
		jToolBar.add(jToggleButton[8]);
		jToolBar.add(jToggleButton[0]);
		jToolBar.add(jToggleButton[4]);
		jToolBar.add(jToggleButton[1]);
		jToolBar.add(jToggleButton[3]);
		jToolBar.add(jToggleButton[2]);
		jToolBar.add(jToggleButton[5]);
		jToolBar.add(jToggleButton[6]);
		jToolBar.add(jToggleButton[9]);
		jToolBar.add(jToggleButton[10]);
		jToggleButton[7].setSelected(true);
		jToolBar.setLayout( new GridLayout( 6, 2, 2, 2 ) );
		jPanel[2].add(jToolBar);
		
		jToolBar.setFloatable(false);
		
		colorPanel=new ColorPanel();
		jPanel[3].setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanel[3].add(colorPanel);
		
		drawPanel=new DrawPanel();
		underDrawPanel=new UnderDrawPanel();
		underDrawPanel.setLayout(null);
		underDrawPanel.add(drawPanel);
		drawPanel.setBounds(new Rectangle(2, 2, draw_panel_width, draw_panel_height));
		
		setPanel=new SetPanel();
		jPanel[4].add(setPanel);
		
		jPanel[0].setLayout( new BorderLayout() );
		jPanel[0].add(underDrawPanel,BorderLayout.CENTER);
		jPanel[0].add(jPanel[2],BorderLayout.WEST);
		jPanel[0].add(jPanel[3],BorderLayout.SOUTH);
		jPanel[0].add(jPanel[4],BorderLayout.EAST);
		
		jLabel[0].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		underDrawPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		underDrawPanel.setBackground(new Color(128,128,128));
		jPanel[3].setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(172,168,153)));
		
		c.add(jPanel[0],BorderLayout.CENTER);
		c.add(jLabel[0],BorderLayout.SOUTH);
		
		setSize(draw_panel_width,draw_panel_height);
		setTitle("画图工具");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		show();
	}
	public void save(){
		FileDialog fileDialog = new FileDialog( new Frame() , "请指定一个文件名", FileDialog.SAVE );
		fileDialog.show();
		if(fileDialog.getFile()==null) return;
		drawPanel.filename = fileDialog.getDirectory()+fileDialog.getFile();
	}
	
	public void actionPerformed( ActionEvent e ){
		for(i=0;i<ButtonName.length;i++){
			if(e.getSource()==jToggleButton[i]){
				drawMethod=i;
				if(drawMethod==5)
					setPanel.pie_add_ctrl();
				else
					setPanel.pie_remove_ctrl();
				if(drawMethod==7 || drawMethod==8)
					setPanel.pencil_add_ctrl();
				else
					setPanel.pencil_remove_ctrl();
				drawPanel.clear();
				drawPanel.repaint();
   				jMenuItem[1][2].setEnabled(false);
   				jMenuItem[1][3].setEnabled(false);
			}
		}
		
		if(e.getSource()==jMenuItem[1][0]){
			drawPanel.undo();
		}
		else if(e.getSource()==jMenuItem[1][1]){
			drawPanel.redo();
		}
		else if(e.getSource()==jMenuItem[1][2]){
			drawPanel.cut();
		}
		else if(e.getSource()==jMenuItem[1][3]){
			drawPanel.copy();
		}
		else if(e.getSource()==jMenuItem[1][4]){
			drawPanel.paste();
		}
		else if(e.getSource()==jMenuItem[0][0]){
			underDrawPanel.remove(drawPanel);
			drawPanel=null;
			drawPanel=new DrawPanel();
			underDrawPanel.add(drawPanel);
			drawPanel.setBounds(new Rectangle(2, 2, draw_panel_width, draw_panel_height));
			underDrawPanel.ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
			underDrawPanel.ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
			underDrawPanel.ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
			repaint();
		}
		else if(e.getSource()==jMenuItem[0][1]){
			FileDialog fileDialog = new FileDialog( new Frame() , "选择一个图片", FileDialog.LOAD );
			fileDialog.show();
			if(fileDialog.getFile()==null) return;
			
			underDrawPanel.removeAll();
			drawPanel=null;
			drawPanel=new DrawPanel();
			underDrawPanel.add(drawPanel);
			drawPanel.setBounds(new Rectangle(2, 2, draw_panel_width, draw_panel_height));
			
			drawPanel.openfile(fileDialog.getDirectory()+fileDialog.getFile());
		}
		else if(e.getSource()==jMenuItem[0][2]){
			if(drawPanel.filename==null){
				save();
			}
			else{
				try{
					int dotpos = drawPanel.filename.lastIndexOf('.');
					ImageIO.write(drawPanel.bufImg, drawPanel.filename.substring(dotpos + 1), new File(drawPanel.filename));
				}
				catch(IOException even) {
					JOptionPane.showMessageDialog(null, even.toString(),"无法存储图片", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(e.getSource()==jMenuItem[0][3]){
			save();
			try{
				int dotpos = drawPanel.filename.lastIndexOf('.');
				ImageIO.write(drawPanel.bufImg, drawPanel.filename.substring(dotpos + 1), new File(drawPanel.filename));
			}
			catch(IOException even) {
				JOptionPane.showMessageDialog(null, even.toString(),"无法储存图片", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource()==jMenuItem[0][4]){
			System.exit(0);
		}
		for(i=0;i<2;i++){
			if(jCheckBoxMenuItem[i].isSelected())
				jPanel[i+2].setVisible( true );
           	else
           		jPanel[i+2].setVisible( false );
       	}
       	if(jCheckBoxMenuItem[3].isSelected()){
       		setPanel.setVisible( true );
       		jPanel[4].setVisible( true );
       	}
       	else{
       		setPanel.setVisible( false );
       		jPanel[4].setVisible( false );
       	}
		if(jCheckBoxMenuItem[2].isSelected())
			jLabel[0].setVisible( true );
       	else
       		jLabel[0].setVisible( false );
	}
	
	public class UnderDrawPanel extends JPanel implements MouseListener, MouseMotionListener{
		public int x,y;
		float data[]={2};
		public JPanel ctrl_area=new JPanel(),ctrl_area2=new JPanel(),ctrl_area3=new JPanel();
		
		public UnderDrawPanel(){
			this.setLayout(null);
			this.add(ctrl_area);
			this.add(ctrl_area2);
			this.add(ctrl_area3);
			
			ctrl_area.setBounds(new Rectangle(draw_panel_width+3, draw_panel_height+3, 5, 5));
			ctrl_area.setBackground(new Color(0,0,0));
			ctrl_area2.setBounds(new Rectangle(draw_panel_width+3, draw_panel_height/2, 5, 5));
			ctrl_area2.setBackground(new Color(0,0,0));
			ctrl_area3.setBounds(new Rectangle(draw_panel_width/2, draw_panel_height+3, 5, 5));
			ctrl_area3.setBackground(new Color(0,0,0));
    		ctrl_area.addMouseListener(this);
    		ctrl_area.addMouseMotionListener(this);
    		ctrl_area2.addMouseListener(this);
    		ctrl_area2.addMouseMotionListener(this);
    		ctrl_area3.addMouseListener(this);
    		ctrl_area3.addMouseMotionListener(this);
		}
		
		public void mouseClicked(MouseEvent e){}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){
			draw_panel_width=x;
			draw_panel_height=y;
			
			ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
			ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
			ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
			drawPanel.setSize(x,y);
			drawPanel.resize();
			repaint();
		}
		
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		
    	public void mouseDragged(MouseEvent e){
    		if(e.getSource()==ctrl_area2){
    			x = e.getX()+draw_panel_width;
    			y = draw_panel_height;
    		}
    		else if(e.getSource()==ctrl_area3){
    			x = draw_panel_width;
    			y = e.getY()+draw_panel_height;
    		}
    		else{
    			x = e.getX()+draw_panel_width;
    			y = e.getY()+draw_panel_height;
    		}
    		repaint();
    		jLabel[0].setText(x+","+y);
    	}
    	public void mouseMoved(MouseEvent e) {}
    	
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paint(g2d);
			
			g2d.setPaint( new Color(128,128,128) );
			g2d.setStroke( new BasicStroke( 1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, data, 0 ) );
			g2d.draw( new Rectangle2D.Double( -1, -1, x+3, y+3 ) );
		}
	}
	
	public class SetPanel extends JPanel implements ItemListener, ChangeListener, ActionListener{
		private	JPanel jPanel_set1=new JPanel();
		private	JPanel jPanel_set2=new JPanel();
		private	JPanel temp0=new JPanel(new GridLayout(4,1)), temp1=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp2=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp3=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp4=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp5=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp6=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp7=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp8=new JPanel(new GridLayout(3,1));

		public JCheckBox jCheckBox = new JCheckBox();
		private BufferedImage bufImg = new BufferedImage(50 ,50,BufferedImage.TYPE_3BYTE_BGR);
		private JLabel jlbImg=new JLabel();
		float data[]={20};
		JLabel pie[]=new JLabel[3];
		public int number=5;
		JSpinner lineWidthSelect = new JSpinner();
		JRadioButton style[] = new JRadioButton[ 5 ];
		ButtonGroup styleGroup = new ButtonGroup() ,pieGroup = new ButtonGroup();
        
		public SetPanel(){
			this.setLayout(null);
			this.add(jPanel_set1);

			jlbImg.setIcon(new ImageIcon(bufImg));
			jPanel_set1.setLayout(new FlowLayout());
			jPanel_set1.setBounds(new Rectangle(0, 0, 100, 160));
			jPanel_set1.setBorder( new TitledBorder(null, "边框",TitledBorder.LEFT, TitledBorder.TOP) );
			lineWidthSelect.setValue(new Integer(5));

			for(i=0;i<=1;i++){
				style[i] = new JRadioButton();
				styleGroup.add(style[i]);
				style[i].addActionListener(this);
			}
			style[0].setSelected( true );
      
			temp1.add(new JLabel("大小:"));
			temp1.add(lineWidthSelect);
			
			temp2.add(new JLabel("虛线:"));
			temp2.add(jCheckBox);
			
			temp3.add(new JLabel("圆角:"));
			temp3.add(style[0]);
			
			temp4.add(new JLabel("尖角:"));
			temp4.add(style[1]);
			
			temp0.add(temp1);
			temp0.add(temp2);
			temp0.add(temp3);
			temp0.add(temp4);
			
			jPanel_set1.add(temp0);
			lineWidthSelect.addChangeListener( this );
			jCheckBox.addItemListener( this );
			
			jPanel_set2.setBounds(new Rectangle(0, 170, 100, 130));
			jPanel_set2.setBorder( new TitledBorder(null, "扇型设定",TitledBorder.LEFT, TitledBorder.TOP) );
			
			for(i=2;i<=4;i++){
				style[i] = new JRadioButton();
				pieGroup.add(style[i]);
				style[i].addActionListener(this);
			}
			style[4].setSelected( true );
			
			pie[0] = new JLabel("弦状:");
			temp5.add(pie[0]);
			temp5.add(style[2]);
			
			pie[1] = new JLabel("开放:");
			temp6.add(pie[1]);
			temp6.add(style[3]);
			
			pie[2] = new JLabel("派状:");
			temp7.add(pie[2]);
			temp7.add(style[4]);
			
			temp8.add(temp5);
			temp8.add(temp6);
			temp8.add(temp7);
			
			temp8.setPreferredSize(new Dimension( 71 , 95 ));

			jPanel_set2.add(temp8);
			this.add(jPanel_set2);

			pie_remove_ctrl();
			stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		}
		
		public void pencil_add_ctrl(){
			style[0].setSelected(true);
			style[1].setEnabled(false);
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(false);
			BasicStroke stroke2 = (BasicStroke) stroke;
			stroke = new BasicStroke(stroke2.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		}
		
		public void pencil_remove_ctrl(){
			style[1].setEnabled(true);
			jCheckBox.setEnabled(true);
		}
		
		public void pie_add_ctrl(){
			pie[0].setEnabled(true);
			pie[1].setEnabled(true);
			pie[2].setEnabled(true);
			style[2].setEnabled(true);
			style[3].setEnabled(true);
			style[4].setEnabled(true);
		}
		
		public void pie_remove_ctrl(){
			pie[0].setEnabled(false);
			pie[1].setEnabled(false);
			pie[2].setEnabled(false);
			style[2].setEnabled(false);
			style[3].setEnabled(false);
			style[4].setEnabled(false);
		}
		
		public void actionPerformed( ActionEvent e ){
			BasicStroke stroke2 = (BasicStroke) stroke;
			if ( e.getSource() == style[0] )
				stroke = new BasicStroke( stroke2.getLineWidth(), BasicStroke.CAP_ROUND, stroke2.getLineJoin(), stroke2.getMiterLimit(), stroke2.getDashArray(), stroke2.getDashPhase() );
			else if ( e.getSource() == style[1] )
				stroke = new BasicStroke( stroke2.getLineWidth(), BasicStroke.CAP_BUTT, stroke2.getLineJoin(), stroke2.getMiterLimit(), stroke2.getDashArray(), stroke2.getDashPhase() );
			else if ( e.getSource() == style[2] )
				drawPanel.pie_shape=Arc2D.CHORD;
			else if ( e.getSource() == style[3] )
				drawPanel.pie_shape=Arc2D.OPEN;
			else if ( e.getSource() == style[4] )
				drawPanel.pie_shape=Arc2D.PIE;
		}
		
		public void stateChanged(ChangeEvent e){
			number = Integer.parseInt(lineWidthSelect.getValue().toString());
			if(number <= 0) {
				lineWidthSelect.setValue(new Integer(1));
				number = 1;
			}
        	BasicStroke stroke2 = (BasicStroke) stroke;
			stroke = new BasicStroke( number, stroke2.getEndCap(), stroke2.getLineJoin(), stroke2.getMiterLimit(), stroke2.getDashArray(), stroke2.getDashPhase() );
		}
		
		public void itemStateChanged( ItemEvent e ){
			BasicStroke stroke2 = (BasicStroke) stroke;
			if ( e.getSource() == jCheckBox ){
				if ( e.getStateChange() == ItemEvent.SELECTED )
					stroke = new BasicStroke( stroke2.getLineWidth(), stroke2.getEndCap(), stroke2.getLineJoin(), 10, data, 0 );
				else
					stroke = new BasicStroke( stroke2.getLineWidth(), stroke2.getEndCap(), stroke2.getLineJoin());
			}
		}
		
		public Dimension getPreferredSize(){
			return new Dimension( 100, 300 );
		}
	}
	
	public class Gradient extends JPanel{
		public Color G_color_left = new Color(255,255,255);
		public Color G_color_right = new Color(0,0,0);
		
		public Gradient(){
			repaint();
		}
		
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setPaint( new GradientPaint( 0, 0, G_color_left, 100, 0, G_color_right, true ) );
			g2d.fill( new Rectangle2D.Double( 0, 0, 100, 25 ) );
		}
		
		public Dimension getPreferredSize(){
			return new Dimension( 100, 25 );
		}
	}
	
	public class ColorPanel extends JPanel implements MouseListener,ActionListener{//調色盤class
		private	JPanel jPanel_color0[]=new JPanel[5];
		private	JPanel jPanel_color1[]=new JPanel[32];
		private	JPanel jPanel_color2[]=new JPanel[32];
		private	ImageIcon special_color[]= new ImageIcon[4];
		private BufferedImage bufImg = new BufferedImage(12 ,12,BufferedImage.TYPE_3BYTE_BGR) ,bufImg2 = new BufferedImage(12 ,12,BufferedImage.TYPE_3BYTE_BGR);
		private JLabel jlbImg=new JLabel() ,jlbImg2=new JLabel();
		private	ImageIcon icon;
		private JDialog jDialog;
		private JButton ok, cancel,left,right;
		private Gradient center = new Gradient();
		
		private	int rgb[][]={
			{0,255,128,192,128,255,128,255,  0,  0,  0,  0,  0,  0,128,255,128,255,  0,  0,  0,128,  0,128,128,255,128,255,255,255,255,255},
			{0,255,128,192,  0,  0,128,255,128,255,128,255,  0,  0,  0,  0,128,255, 64,255,128,255, 64,128,  0,  0, 64,128,255,255,255,255},
			{0,255,128,192,  0,  0,  0,  0,  0,  0,128,255,128,255,128,255, 64,128, 64,128,255,255,128,255,255,128,  0, 64,255,255,255,255}
		};
		
		public ColorPanel(){//產生版面//
			addMouseListener( this );
			jlbImg.setIcon(new ImageIcon(bufImg));
			jlbImg2.setIcon(new ImageIcon(bufImg2));

			special_color[0] = new ImageIcon( "src/img/icon1.gif" );
			special_color[1] = new ImageIcon( "src/img/icon2.gif" );
			special_color[2] = new ImageIcon( "src/img/icon3.gif" );
			special_color[3] = new ImageIcon( "src/img/icon4.gif" );
			
			this.setLayout(null);
			color_border=new Color(0,0,0);
			color_inside=null;
			
			for(i=0;i<jPanel_color0.length;i++){
				jPanel_color0[i]=new JPanel();
				if(i<=2){
					jPanel_color0[i].setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
					jPanel_color0[i].setLayout(null);
				}
				else{
					jPanel_color0[i].setBackground(new Color(rgb[0][i-3],rgb[1][i-3],rgb[2][i-3]));
					jPanel_color0[i].setLayout(new GridLayout(1,1));
					jPanel_color0[i-2].add(jPanel_color0[i]);
				}
			}
			for(i=0;i<jPanel_color2.length;i++){
				jPanel_color2[i]=new JPanel();
				jPanel_color2[i].setLayout(new GridLayout(1,1));
				jPanel_color2[i].setBounds(new Rectangle(2, 2, 12, 12));
				jPanel_color2[i].setBackground(new Color(rgb[0][i],rgb[1][i],rgb[2][i]));
				if(i>=28)
					jPanel_color2[i].add(new JLabel(special_color[i-28]));
			}
			
			for(i=0;i<jPanel_color1.length;i++){
				jPanel_color1[i]=new JPanel();
				jPanel_color1[i].setLayout(null);
				jPanel_color1[i].add(jPanel_color2[i]);
				this.add(jPanel_color1[i]);
				if(i%2==0){jPanel_color1[i].setBounds(new Rectangle(32+i/2*16, 0, 16, 16));}
				else{jPanel_color1[i].setBounds(new Rectangle(32+i/2*16, 16, 16, 16));}
				jPanel_color1[i].setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
			}
			
			jPanel_color0[3].add(jlbImg);
			jPanel_color0[4].add(jlbImg2);
			
			Graphics2D g2d = bufImg2.createGraphics();
			g2d.setPaint( Color.white );
			g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
			g2d.setPaint( Color.red ); 
			g2d.draw( new Line2D.Double( 0, 0, 12, 12 ) );
			g2d.draw( new Line2D.Double( 11, 0, 0, 11 ) );
			repaint();
			
			this.add(jPanel_color0[1]);
			this.add(jPanel_color0[2]);
			this.add(jPanel_color0[0]);

			jPanel_color0[0].setBounds(new Rectangle(0, 0, 32, 32));
			jPanel_color0[1].setBounds(new Rectangle(4, 4, 16, 16));
			jPanel_color0[2].setBounds(new Rectangle(12,12,16, 16));
			jPanel_color0[3].setBounds(new Rectangle(2, 2, 12, 12));
			jPanel_color0[4].setBounds(new Rectangle(2, 2, 12, 12));
			
			jDialog = new JDialog(project4.this, "請選擇兩種顏色做漸層", true);
        	jDialog.getContentPane().setLayout(new FlowLayout());
        	jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE );
        	jDialog.setSize(250, 110);
        	JPanel temp = new JPanel(new GridLayout(2,1));
        	JPanel up = new JPanel(new FlowLayout());
        	JPanel down = new JPanel(new FlowLayout());
        	
			ok = new JButton("確定");
			cancel = new JButton("取消");
			left = new JButton(" ");
			right = new JButton(" ");
			center.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
			up.add(left);
			up.add(center);
			up.add(right);
			down.add(ok);
			down.add(cancel);
			temp.add(up);
			temp.add(down);
			jDialog.getContentPane().add(temp);
			
			ok.addActionListener(this);
			cancel.addActionListener(this);
			left.addActionListener(this);
			right.addActionListener(this);
		}
		public void actionPerformed( ActionEvent e ){
			if(e.getSource() == left){
				center.G_color_left = JColorChooser.showDialog( project4.this, "请选择边缘颜色", center.G_color_left );
				center.repaint();
			}
			else if(e.getSource() == right){
				center.G_color_right = JColorChooser.showDialog( project4.this, "请选择边缘颜色", center.G_color_right );
				center.repaint();
			}
			else{
				jDialog.dispose();
			}
		}
		
		public Dimension getPreferredSize(){
			return new Dimension( 300, 32 );
		}
		public void mouseClicked( MouseEvent e ){}
		public void mousePressed( MouseEvent e ){
			Graphics2D g2d;
			if(e.getX()>=5 && e.getX()<=20 && e.getY()>=5 && e.getY()<=20){
				g2d = bufImg.createGraphics();
				color_border = JColorChooser.showDialog( project4.this, "请选择边缘颜色", (Color)color_border );
				g2d.setPaint(color_border);
				g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
				repaint();
			}
			else if(e.getX()>=13 && e.getX()<=28 && e.getY()>=13 && e.getY()<=28){
				g2d = bufImg2.createGraphics();
				color_inside = JColorChooser.showDialog( project4.this, "请选择边缘颜色", (Color)color_inside );
				g2d.setPaint(color_inside);
				g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
				repaint();
			}
			
			if(!(e.getX()>=32 && e.getX()<=288)) return;
			int choose=(e.getX()-32)/16*2+e.getY()/16;
			
			if(e.getButton()==1)
				g2d = bufImg.createGraphics();
			else
				g2d = bufImg2.createGraphics();
			
			if(choose==28){
				g2d.setPaint( Color.white );
				g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
				g2d.setPaint( Color.red ); 
				g2d.draw( new Line2D.Double( 0, 0, 12, 12 ) );
				g2d.draw( new Line2D.Double( 11, 0, 0, 11 ) );
				repaint();
					
				if(e.getButton()==1)
					color_border=null;
				else
					color_inside=null;
			}
			else if(choose==29){
				jDialog.show();
				
				g2d.setPaint( new GradientPaint( 0, 0, center.G_color_left, 12, 12, center.G_color_right, true ) );
				g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
				repaint();
				
				if(e.getButton()==1)
					color_border=new GradientPaint( 0, 0, center.G_color_left, 12, 12, center.G_color_right, true );
				else
					color_inside=new GradientPaint( 0, 0, center.G_color_left, 12, 12, center.G_color_right, true );
			}
			else if(choose==30){
				FileDialog fileDialog = new FileDialog( new Frame() , "选择一个图片", FileDialog.LOAD );//利用FileDialog抓取檔名
				fileDialog.show();//秀出視窗
				if(fileDialog.getFile()==null) return;//按取消的處理
				
				g2d.drawImage(special_color[2].getImage(), 0, 0,this);//把調色盤左方換成『圖片』
				
				icon = new ImageIcon(fileDialog.getDirectory());//利用FileDialog傳進來的檔名讀取圖片
				BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_3BYTE_BGR);//創一張新的BufferedImage，為了要讀取讀進來的圖片長寬，以免有空白
				bufferedImage.createGraphics().drawImage(icon.getImage(),0,0,this);//把icon畫到BufferedImage上
				repaint();//重繪螢幕
				
				if(e.getButton()==1)//判斷邊線顏色或內部填滿色
					color_border=new TexturePaint(bufferedImage, new Rectangle( icon.getIconWidth(), icon.getIconHeight() ) );//把這張BufferedImage設成TexturePaint來填滿
				else
					color_inside=new TexturePaint(bufferedImage, new Rectangle( icon.getIconWidth(), icon.getIconHeight() ) );
			}
			else if(choose==31){//填充文字
				String text=JOptionPane.showInputDialog("請輸入文字","文字");//輸入文字
				if(text==null) return;//按取消時的處理
				
				Color FontColor=new Color(0,0,0);//給這個字顏色
				FontColor = JColorChooser.showDialog( project4.this, "請選擇一個顏色當文字顏色", FontColor );//讓使用者選擇顏色
				
				g2d.drawImage(special_color[3].getImage(), 0, 0,this);//把調色盤左方換成『字』
				
				BufferedImage bufferedImage = new BufferedImage(draw_panel_width,draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);//創一張新的BufferedImage
				Graphics2D g2d_bufferedImage = bufferedImage.createGraphics();//把Graphics拿出來畫
				
				FontRenderContext frc = g2d_bufferedImage.getFontRenderContext();//讀Graphics中的Font
				Font f = new Font("新細明體",Font.BOLD,10);//新Font
				TextLayout tl = new TextLayout(text, f, frc);//創新的TextLayout，並利用f(Font)跟畫至於frc(FontRenderContext)
				
				int sw = (int) (tl.getBounds().getWidth()+tl.getCharacterCount());//計算TextLayout的長
				int sh = (int) (tl.getBounds().getHeight()+3);//計算TextLayout的高
				
				bufferedImage = new BufferedImage(sw,sh,BufferedImage.TYPE_3BYTE_BGR);//再創一張新的BufferedImage，這裡利用相同指標指向不同記憶體
				g2d_bufferedImage = bufferedImage.createGraphics();//拿出Graphics來畫，前一張BufferedImage只是為了計算文字長度與高度，這樣才能完整填滿
				
				g2d_bufferedImage.setPaint(Color.WHITE);//設定顏色為白色
				g2d_bufferedImage.fill(new Rectangle(0,0,sw,sh));//畫一個填滿白色矩型
				g2d_bufferedImage.setPaint(FontColor);//設定顏色為之前選擇文字顏色
				g2d_bufferedImage.drawString(text,0,10);//畫一個String於BufferedImage上
				repaint();//更新畫面
				
				if(e.getButton()==1)//判斷邊線顏色或內部填滿色
					color_border=new TexturePaint(bufferedImage, new Rectangle(sw,sh) );//把這張BufferedImage設成TexturePaint來填滿
				else
					color_inside=new TexturePaint(bufferedImage, new Rectangle(sw,sh) );
			}
			else{//填充一般色
				g2d.setPaint(new Color(rgb[0][choose],rgb[1][choose],rgb[2][choose]));
				g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
				repaint();
				
				if(e.getButton()==1)
					color_border=new Color(rgb[0][choose],rgb[1][choose],rgb[2][choose]);
				else
					color_inside=new Color(rgb[0][choose],rgb[1][choose],rgb[2][choose]);
			}
		}

		public void mouseReleased( MouseEvent e ){}
		public void mouseEntered( MouseEvent e ){}
		public void mouseExited( MouseEvent e ){}
	}
	
	public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, ItemListener, ActionListener, ChangeListener{//中央畫布
		public BufferedImage bufImg;//記錄最新畫面，並在此上作畫
		private BufferedImage bufImg_data[];//記錄所有畫出圖面，索引值越大越新，最大為最新
		private BufferedImage bufImg_cut;
		private ImageIcon img;
		private JLabel jlbImg;
		private int x1=-1,y1=-1,x2,y2,count,redo_lim,press,temp_x1,temp_y1,temp_x2,temp_y2,temp_x3,temp_y3,step,step_chk,step_arc,step_chk_arc,chk,first,click,cut;
		private Arc2D.Double arc2D = new Arc2D.Double();//扇型
		private Line2D.Double line2D = new Line2D.Double();//直線
		private Ellipse2D.Double ellipse2D = new Ellipse2D.Double();//橢圓
		private Rectangle2D.Double rectangle2D = new Rectangle2D.Double();//矩型
		private CubicCurve2D.Double cubicCurve2D = new CubicCurve2D.Double();//貝氏曲線
		private RoundRectangle2D.Double roundRectangle2D = new RoundRectangle2D.Double();//圓角矩型
		private Polygon polygon;//多邊型
		private float data[]={5};
		private Rectangle2D.Double rectangle2D_select = new Rectangle2D.Double();//矩型
		private Ellipse2D.Double ellipse2D_pan = new Ellipse2D.Double();
		private BasicStroke basicStroke_pen = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		private BasicStroke basicStroke_select = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,10, data, 0);
		private double center_point_x;
		private double center_point_y;
		private double start;
		private double end;
		public String filename;
		private JTextField textField_font = new JTextField("Fixedsys",16), textField_word = new JTextField("JAVA",16);
		private int size=100;
		private JSpinner fontsize = new JSpinner();
		private JDialog jDialog;
		private JCheckBox bold, italic;
		private JButton ok, cancel;
		public int pie_shape=Arc2D.PIE;
		private int valBold = Font.BOLD;
		private int valItalic = Font.ITALIC;
		private int select_x,select_y,select_w,select_h;
		
		public void resize(){//改變大小
			bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
			jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用來繪圖
			this.removeAll();
			this.add(jlbImg);
			jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
			
    		//畫出原本圖形//
    		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    		g2d_bufImg.setPaint(Color.white);
    		g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
    		g2d_bufImg.drawImage(bufImg_data[count],0,0,this);

			//記錄可重做最大次數，並讓重做不可按//
			redo_lim=count++;
			jMenuItem[1][1].setEnabled(false);
			
   			//新增一張BufferedImage型態至bufImg_data[count]，並將bufImg繪製至bufImg_data[count]//
   			bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
   			Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
   			g2d_bufImg_data.drawImage(bufImg,0,0,this);
   			
			//判斷座標為新起點//
			press=0;
			
			//讓復原MenuItem可以點選//
   			if(count>0)
   				jMenuItem[1][0].setEnabled(true);
		}
		
		public DrawPanel() {
			bufImg_data = new BufferedImage[1000];
			bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
			jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用來繪圖

			this.setLayout(null);
			this.add(jlbImg);
			jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
			
		 	jMenuItem[1][0].setEnabled(false);
    		jMenuItem[1][1].setEnabled(false);
    		jMenuItem[1][2].setEnabled(false);
    		jMenuItem[1][3].setEnabled(false);
    		jMenuItem[1][4].setEnabled(false);
    		
    		//畫出空白//
    		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    		g2d_bufImg.setPaint(Color.WHITE);
			g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
			
    		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
    		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
    		g2d_bufImg_data.drawImage(bufImg,0,0,this);
			
			//Font//
			jDialog = new JDialog(project4.this, "請選擇文字、字型、大小與屬性", true);
			fontsize.setValue(new Integer(100));
			bold = new JCheckBox( "粗體" ,true);
			italic = new JCheckBox( "斜體" ,true);
			ok = new JButton("確定");
			cancel = new JButton("取消");
			JPanel temp_0 = new JPanel(new GridLayout(5,1));
			JPanel temp_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel temp_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel temp_3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel temp_4 = new JPanel(new FlowLayout());
			JPanel temp_5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			Container jDialog_c = jDialog.getContentPane();
				
        	jDialog_c.setLayout(new FlowLayout());
        	jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE );
        	jDialog.setSize(250, 200);
        	temp_5.add(new JLabel("文字:"));
			temp_5.add(textField_word);
			temp_1.add(new JLabel("字體:"));
			temp_1.add(textField_font);
			temp_2.add(new JLabel("大小:"));
			temp_2.add(fontsize);
			temp_3.add(new JLabel("屬性:"));
			temp_3.add(bold);
			temp_3.add(italic);
			temp_4.add(ok);
			temp_4.add(cancel);
			temp_0.add(temp_5);
			temp_0.add(temp_1);
        	temp_0.add(temp_2);
        	temp_0.add(temp_3);
        	temp_0.add(temp_4);
        	jDialog_c.add(temp_0);
        	
        	bold.addItemListener( this );
        	italic.addItemListener( this );
        	fontsize.addChangeListener( this );
        	ok.addActionListener(this);
        	cancel.addActionListener(this);
        	temp_0.setPreferredSize(new Dimension( 180 , 150 ));
        	
			repaint();
    		addMouseListener(this);
    		addMouseMotionListener(this);
		}
		public void stateChanged(ChangeEvent e){
			size = Integer.parseInt(fontsize.getValue().toString());
			if(size <= 0) {
				fontsize.setValue(new Integer(1));
				size = 1;
			}
		}
		
		public void actionPerformed( ActionEvent e ){
			jDialog.dispose();
		}
		public void itemStateChanged( ItemEvent e ){
			if ( e.getSource() == bold )
				if ( e.getStateChange() == ItemEvent.SELECTED )
					valBold = Font.BOLD;
				else
					valBold = Font.PLAIN;
			if ( e.getSource() == italic )
				if ( e.getStateChange() == ItemEvent.SELECTED )
					valItalic = Font.ITALIC;
				else
					valItalic = Font.PLAIN;
		}
		
		public Dimension getPreferredSize(){
			return new Dimension( draw_panel_width, draw_panel_height );
		}
		
		public void openfile(String filename){//開啟舊檔
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
			ImageIcon icon = new ImageIcon(filename);
			g2d_bufImg.drawImage(icon.getImage(),0,0,this);
			
			count++;
    		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
    		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
    		g2d_bufImg_data.drawImage(bufImg,0,0,this);
			
			repaint();
		}
		
		public void undo(){//復原
   			count--;
			
   			draw_panel_width=bufImg_data[count].getWidth();
   			draw_panel_height=bufImg_data[count].getHeight();
   			drawPanel.setSize(draw_panel_width,draw_panel_height);

			bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
			jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用來繪圖
			this.removeAll();
			this.add(jlbImg);
			jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
			
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    		g2d_bufImg.setPaint(Color.white);
    		g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
			g2d_bufImg.drawImage(bufImg_data[count],0,0,this);

			underDrawPanel.ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
			underDrawPanel.ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
			underDrawPanel.ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
			
			underDrawPanel.x=draw_panel_width;
			underDrawPanel.y=draw_panel_height;
			
	   		if(count<=0)
	   			jMenuItem[1][0].setEnabled(false);
	    	jMenuItem[1][1].setEnabled(true);
	    	cut=3;
   			repaint();
   		}

		public void redo(){//重做
			count++;
			
   			draw_panel_width=bufImg_data[count].getWidth();
   			draw_panel_height=bufImg_data[count].getHeight();
   			drawPanel.setSize(draw_panel_width,draw_panel_height);

			bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
			jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用來繪圖
			this.removeAll();
			this.add(jlbImg);
			jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
			
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    		g2d_bufImg.setPaint(Color.white);
    		g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
			g2d_bufImg.drawImage(bufImg_data[count],0,0,this);

			underDrawPanel.ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
			underDrawPanel.ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
			underDrawPanel.ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
			
			underDrawPanel.x=draw_panel_width;
			underDrawPanel.y=draw_panel_height;
			
			if(redo_lim<count)
				jMenuItem[1][1].setEnabled(false);
			jMenuItem[1][0].setEnabled(true);
			cut=3;
   			repaint();
   		}
		
		public void cut(){
			bufImg_cut = new BufferedImage((int)rectangle2D_select.getWidth(), (int)rectangle2D_select.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			BufferedImage copy = bufImg.getSubimage((int)rectangle2D_select.getX(),(int)rectangle2D_select.getY(),(int)rectangle2D_select.getWidth(),(int)rectangle2D_select.getHeight());
			Graphics2D g2d_bufImg_cut = (Graphics2D) bufImg_cut.createGraphics();
			g2d_bufImg_cut.drawImage(copy,0,0,this);
			
    		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    		g2d_bufImg.setPaint(Color.WHITE);
			g2d_bufImg.fill(new Rectangle2D.Double((int)rectangle2D_select.getX(),(int)rectangle2D_select.getY(),(int)rectangle2D_select.getWidth(),(int)rectangle2D_select.getHeight()));
			
			redo_lim=count++;
			jMenuItem[1][1].setEnabled(false);
			
   			//新增一張BufferedImage型態至bufImg_data[count]，並將bufImg繪製至bufImg_data[count]//
   			bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
   			Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
   			g2d_bufImg_data.drawImage(bufImg,0,0,this);

			//判斷座標為新起點//
			press=0;
			
			//讓復原MenuItem可以點選//
   			if(count>0)
   				jMenuItem[1][0].setEnabled(true);
   			jMenuItem[1][2].setEnabled(false);
   			jMenuItem[1][3].setEnabled(false);
			jMenuItem[1][4].setEnabled(true);
			cut=3;
			repaint();
		}
		public void copy(){
			bufImg_cut = new BufferedImage((int)rectangle2D_select.getWidth(), (int)rectangle2D_select.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			BufferedImage copy = bufImg.getSubimage((int)rectangle2D_select.getX(),(int)rectangle2D_select.getY(),(int)rectangle2D_select.getWidth(),(int)rectangle2D_select.getHeight());
			Graphics2D g2d_bufImg_cut = (Graphics2D) bufImg_cut.createGraphics();
			g2d_bufImg_cut.drawImage(copy,0,0,this);
			jMenuItem[1][4].setEnabled(true);
			cut=1;
			repaint();
		}
		public void paste(){
			cut=2;
			repaint();
		}
    	public void mousePressed(MouseEvent e) {
    		x1=e.getX();
    		y1=e.getY();
    		if(first==0){
    			polygon = new Polygon();
				polygon.addPoint(x1, y1);
				first=1;
			}
			//判斷座標為新起點//
			press=1;
			chk=0;
			if(cut!=2) cut=0;
		}

    	public void mouseReleased(MouseEvent e) {
    		x2=e.getX();
    		y2=e.getY();
    		
    		if(step_chk==0)//控制貝氏曲線用
    			step=1;
    		else if(step_chk==1)
    			step=2;
    		
    		if(step_chk_arc==0)//控制扇型用
    			chk=step_arc=1;
    		else if(step_chk_arc==1)
    			chk=step_arc=2;
			
			if(drawMethod==6 && click!=1){
				polygon.addPoint(x2, y2);
				repaint();
			}
			if(drawMethod==10){
				if(cut!=2) cut=1;
				select_x=(int)rectangle2D_select.getX();
				select_y=(int)rectangle2D_select.getY();
				select_w=(int)rectangle2D_select.getWidth();
				select_h=(int)rectangle2D_select.getHeight();
				jMenuItem[1][2].setEnabled(true);
				jMenuItem[1][3].setEnabled(true);
			}

    		if((step_chk==2 && step==2) || (step_chk_arc==2 && step_arc==2) || drawMethod==0 || drawMethod==1 || drawMethod==2 || drawMethod==3 || drawMethod==7 || drawMethod==8 || drawMethod==9 || cut==2){//當不是畫貝氏曲線或是已經完成貝氏曲線時畫
				toDraw();
    		}
		}
		public void clear(){
			cut=select_x=select_y=select_w=select_h=step_chk_arc=step_arc=first=step_chk=step=0;
			x1=x2=y1=y2=-1;
		}
		
		public void toDraw(){
			if(x1<0 || y1<0) return;//防止誤按
			chk=3;
			draw(x1,y1,x2,y2);
			
			//畫出圖形至bufImg//
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
			if(cut!=2){
				if(color_inside!=null && drawMethod!=8){
					g2d_bufImg.setPaint(color_inside);
					g2d_bufImg.fill(shape);
				}
				if(color_border!=null && drawMethod!=8){
					g2d_bufImg.setPaint(color_border);
					g2d_bufImg.setStroke(stroke);
					g2d_bufImg.draw(shape);
				}
			}
			else{
   				g2d_bufImg.drawImage(bufImg_cut,x2,y2,this);
			}
			repaint();
			clear();
			//記錄可重做最大次數，並讓重做不可按//
			redo_lim=count++;
			jMenuItem[1][1].setEnabled(false);
			
   			//新增一張BufferedImage型態至bufImg_data[count]，並將bufImg繪製至bufImg_data[count]//
   			bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
   			Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
   			g2d_bufImg_data.drawImage(bufImg,0,0,this);
   			
			//判斷座標為新起點//
			press=0;
			
			//讓復原MenuItem可以點選//
   			if(count>0)
   				jMenuItem[1][0].setEnabled(true);
		}
		
    	public void mouseEntered(MouseEvent e){}
    	public void mouseExited(MouseEvent e){}
    	public void mouseClicked(MouseEvent e){
    		if(click==1){//連點兩下時
    			toDraw();
    		}
    		click=1;
    	}
    	
    	public void mouseDragged(MouseEvent e){
    		x2=e.getX();
    		y2=e.getY();
    		if(drawMethod==7 || drawMethod==8){
				draw(x1,y1,x2,y2);
				x1=e.getX();
				y1=e.getY();
			}
			if(drawMethod!=9)
    			repaint();
    	}

    	public void mouseMoved(MouseEvent e) {
			show_x=x2=e.getX();
			show_y=y2=e.getY();
    		
			jLabel[0].setText(show_x+","+show_y);
			click=0;
			if(drawMethod==7 || drawMethod==8 || cut==2)
				repaint();
    	}
		
		public void draw(int input_x1,int input_y1,int input_x2,int input_y2){
			if(drawMethod==0){//直線時，讓shape為Line2D
				shape=line2D;
				line2D.setLine(input_x1,input_y1,input_x2,input_y2);
			}
			else if(drawMethod==1){//矩型時，讓shape為Rectangle2D
				shape=rectangle2D;
				rectangle2D.setRect(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
			}
			else if(drawMethod==2){//橢圓時
				shape=ellipse2D;
				ellipse2D.setFrame(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
			}
			else if(drawMethod==3){//圓角矩型
				shape=roundRectangle2D;
				roundRectangle2D.setRoundRect(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2),10.0f,10.0f);
			}
			else if(drawMethod==4){//貝氏曲線
				shape=cubicCurve2D;
				if(step==0){
					cubicCurve2D.setCurve(input_x1,input_y1,input_x1,input_y1,input_x2,input_y2,input_x2,input_y2);
					temp_x1=input_x1;
					temp_y1=input_y1;
					temp_x2=input_x2;
					temp_y2=input_y2;
					step_chk=0;
				}
				else if(step==1){
					cubicCurve2D.setCurve(temp_x1,temp_y1,input_x2,input_y2,input_x2,input_y2,temp_x2,temp_y2);
					temp_x3=input_x2;
					temp_y3=input_y2;
					step_chk=1;
				}
				else if(step==2){
					cubicCurve2D.setCurve(temp_x1,temp_y1,temp_x3,temp_y3,input_x2,input_y2,temp_x2,temp_y2);
					step_chk=2;
				}
			}
			else if(drawMethod==5){//扇型，chk用來防止意外的repaint//
				if(step_arc==0 || chk==1){//步驟控制
					shape=ellipse2D;
					ellipse2D.setFrame(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
					temp_x1=input_x1;
					temp_y1=input_y1;
					temp_x2=input_x2;
					temp_y2=input_y2;
					step_chk_arc=0;
				}
				else if(step_arc==1 || chk==2){//步驟控制
					shape=arc2D;

					center_point_x = Math.min(temp_x1,temp_x2)+Math.abs(temp_x1-temp_x2)/2;
					center_point_y = Math.min(temp_y1,temp_y2)+Math.abs(temp_y1-temp_y2)/2;
					
					double a = Math.pow(Math.pow(input_x2-center_point_x,2)+Math.pow(input_y2-center_point_y,2),0.5);
					double b = input_x2-center_point_x;
					if(input_y2>center_point_y)
						start=360+Math.acos(b/a)/Math.PI*-180;
					else
						start=Math.acos(b/a)/Math.PI*180;
					
					arc2D.setArc(Math.min(temp_x1,temp_x2),Math.min(temp_y1,temp_y2),Math.abs(temp_x1-temp_x2),Math.abs(temp_y1-temp_y2),start,0,pie_shape);
					step_chk_arc=1;
				}
				else if(step_arc==2 || chk==3){//步驟控制
					shape=arc2D;
					
					double a = Math.pow(Math.pow(input_x2-center_point_x,2)+Math.pow(input_y2-center_point_y,2),0.5);
					double b = input_x2-center_point_x;
					if(input_y2>center_point_y)
						end=360+Math.acos(b/a)/Math.PI*-180-start;
					else
						end=Math.acos(b/a)/Math.PI*180-start;
					if(end<0){end=360-Math.abs(end);}
					
					arc2D.setArc(Math.min(temp_x1,temp_x2),Math.min(temp_y1,temp_y2),Math.abs(temp_x1-temp_x2),Math.abs(temp_y1-temp_y2),start,end,pie_shape);
					step_chk_arc=2;
				}
			}
			else if(drawMethod==6){//多邊型
				shape=polygon;
			}
			else if(drawMethod==7 || drawMethod==8){//任意線＆橡皮擦
    			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    			
    			shape=line2D;
				line2D.setLine(input_x1,input_y1,input_x2,input_y2);
				if(drawMethod==7)
					g2d_bufImg.setPaint(color_border);
				else
					g2d_bufImg.setPaint(Color.white);
				g2d_bufImg.setStroke(stroke);
				g2d_bufImg.draw(shape);
			}
			
			else if(drawMethod==9){//文字
				Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
        		FontRenderContext frc = g2d_bufImg.getFontRenderContext();
        		jDialog.show();
        		
        		Font f = new Font(textField_font.getText(),valBold + valItalic,size);
        		TextLayout tl = new TextLayout(textField_word.getText(), f, frc);
        		double sw = tl.getBounds().getWidth();
        		double sh = tl.getBounds().getHeight();

        		AffineTransform Tx = AffineTransform.getScaleInstance(1, 1);
        		Tx.translate(input_x2,input_y2+sh);
        		shape = tl.getOutline(Tx);
			}
			else if(drawMethod==10){//選取工具
				shape=rectangle2D;
				rectangle2D.setRect(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
			}
			if(color_border instanceof GradientPaint){//使用漸層填色讀取拖拉座標
				color_border = new GradientPaint( input_x1,input_y1, (Color)((GradientPaint)color_border).getColor1(), input_x2,input_y2, (Color)((GradientPaint)color_border).getColor2(), true );
			}
			if(color_inside instanceof GradientPaint){
				color_inside = new GradientPaint( input_x1,input_y1, (Color)((GradientPaint)color_inside).getColor1(), input_x2,input_y2, (Color)((GradientPaint)color_inside).getColor2(), true );
			}
		}
		
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paint(g2d);//重繪底層JPanel以及上面所有元件

			if(press==1 && drawMethod!=10 && !(x1<0 || y1<0)) {//繪圖在最上面的JLabel上，並判斷是不是起點才畫
				draw(x1,y1,x2,y2);
				if(drawMethod==8) return;
				if(color_inside!=null){
					g2d.setPaint(color_inside);
					g2d.fill(shape);
				}
				if(color_border!=null){
					g2d.setPaint(color_border);
					g2d.setStroke(stroke);
					g2d.draw(shape);
				}
			}

			if(drawMethod==10 && cut==0){//選取控制、判斷是否選取、剪下、或貼上
				g2d.setPaint(Color.black);
				g2d.setStroke(basicStroke_select);
				rectangle2D_select.setRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
				g2d.draw(rectangle2D_select);
			}
			if(cut==1){
				g2d.setPaint(Color.black);
				g2d.setStroke(basicStroke_select);
				rectangle2D_select.setRect(select_x,select_y,select_w,select_h);
				g2d.draw(rectangle2D_select);
			}
			if(cut==2){
   				g2d.drawImage(bufImg_cut,x2,y2,this);
   			}

			//跟隨游標的圓形//
			if(drawMethod==7 || drawMethod==8){
				g2d.setPaint(Color.black);
				g2d.setStroke(basicStroke_pen);
				ellipse2D_pan.setFrame(x2-setPanel.number/2,y2-setPanel.number/2,setPanel.number,setPanel.number);
				g2d.draw(ellipse2D_pan);
			}
		}
	}

	public static void main( String args[] ){
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch(Exception e){e.printStackTrace();}
		
		project4 app = new project4();
		app.setVisible(true);
		app.setExtendedState(Frame.MAXIMIZED_BOTH);
	}
}

