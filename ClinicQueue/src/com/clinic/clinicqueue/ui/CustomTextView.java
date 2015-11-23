package com.clinic.clinicqueue.ui;
  
import android.annotation.SuppressLint;
import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Matrix;  
import android.graphics.Paint;  
import android.graphics.RectF;  
import android.graphics.Paint.Align;  
import android.graphics.Paint.FontMetrics;  
import android.graphics.drawable.Drawable;  
import android.os.Handler;
import android.os.Message;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;  
import android.util.DisplayMetrics;  
import android.view.View;  
import android.widget.LinearLayout;

public class CustomTextView extends View {  
    private String text;
    private int textColor;  
    private int textSize;  
    private int textAlign;
    
    private String deptName;
    private String waitNum;
    private String patientName;
    private String deptLocation;
    private String visitNum;
      
    public static final int TEXT_ALIGN_LEFT              = 0x00000001;  
    public static final int TEXT_ALIGN_RIGHT             = 0x00000010;  
    public static final int TEXT_ALIGN_CENTER_VERTICAL   = 0x00000100;  
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;  
    public static final int TEXT_ALIGN_TOP               = 0x00010000;  
    public static final int TEXT_ALIGN_BOTTOM            = 0x00100000;  
    
    private static final int MAX_SHINE_COUNT = 5;
      
    private float textCenterX;  
    private float textBaselineY;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private boolean scrolling = false;
    private boolean multiline = false;
    private float textWidth = 0.0f;
    private float textHeight = 0.0f;
    private byte textType = 0;
    private boolean shinning = false;
    private int nShinCount = 0;
      
    private int viewWidth;  
    private int viewHeight;  
    private TextPaint paint;  
      
    private FontMetrics fm;  
    private Context context;
    private Matrix matrix;
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case 0:
    			if (offsetX < -textWidth) {
    				offsetX = viewWidth;
    				invalidate();
    				if(scrolling) sendEmptyMessageDelayed(0, 160);
    			} else {
    				offsetX -= 10;
    				invalidate();
    				if(scrolling) sendEmptyMessageDelayed(0, 160);
    			}
    		break;
    		case 1:
    			if (offsetY < -textHeight) {
    				offsetY = viewHeight;
    				invalidate();
    				if(scrolling) sendEmptyMessageDelayed(1, 160);
    			} else {
    				offsetY -= 10;
    				invalidate();
    				if(scrolling) sendEmptyMessageDelayed(1, 160);
    			}
    		default:
    		case 2:
    			if(nShinCount<MAX_SHINE_COUNT)
    			{
    				shinning = !shinning;
    				sendEmptyMessageDelayed(2, 500);
    			}
    			invalidate();
    			nShinCount++;
    		break;
    		}
    		super.handleMessage(msg);
    	}
    };

    public CustomTextView(Context context) {  
        super(context);  
        this.context = context;  
        init();  
    }
  
    public CustomTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        this.context = context;  
        init();  
    }
    
    public void setMultiLine(boolean multi){
    	multiline = multi;
    }
    
    public void doShine(){
    	nShinCount = MAX_SHINE_COUNT;
    	if (mHandler.hasMessages(2))  
            mHandler.removeMessages(2);
    	shinning = false;
    	nShinCount = 0; 
    	mHandler.sendEmptyMessageDelayed(2, 500);
    	invalidate(); 
    }
    
    private void init() {
    	matrix = new Matrix();
        paint = new TextPaint();
        paint.setAntiAlias(true);  
        paint.setTextAlign(Align.CENTER);  
        textAlign = TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL;  
        this.textColor = Color.BLACK;  
    }  
      
    @Override  
    protected void onLayout(boolean changed, int left, int top, int right,  
            int bottom) {  
        viewWidth = getWidth();  
        viewHeight = getHeight();  
        super.onLayout(changed, left, top, right, bottom);  
    }  
      
    @Override  
    protected void onDraw(Canvas canvas) {
    	paint.setTextSize(textSize);  
        paint.setColor(shinning ? Color.RED : textColor);
        fm = paint.getFontMetrics();
        switch(textType)
        {
        case 0:
        	if(multiline)
            {
            	StaticLayout layout = new StaticLayout(text,paint,viewWidth,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
            	int totalLineCount = layout.getLineCount();
            	if(totalLineCount > 1)
            	{
            		textWidth = viewWidth;
            		textHeight = layout.getLineBottom(totalLineCount-1);
            		setTextLocation();
                	canvas.translate(textCenterX+offsetX, textBaselineY+offsetY);
                	layout.draw(canvas);
            	}
            	else
            	{
            		textWidth = paint.measureText(text);
                    textHeight = (fm.descent - fm.ascent);
                	setTextLocation();
                	canvas.drawText(text, textCenterX+offsetX, textBaselineY+offsetY - fm.ascent, paint); 
            	}
            }
            else
            {
            	textWidth = paint.measureText(text);
                textHeight = (fm.descent - fm.ascent);
            	setTextLocation();
            	canvas.drawText(text, textCenterX+offsetX, textBaselineY+offsetY - fm.ascent, paint); 
            }
        	break;
        case 1:
        	paint.setTextAlign(Align.LEFT);
        	StaticLayout patlayout = new StaticLayout(visitNum,paint,viewWidth*3/20,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
    		canvas.translate(0, 0);
    		patlayout.draw(canvas);
    		paint.setTextAlign(Align.RIGHT);
    		patlayout = new StaticLayout(patientName,paint,viewWidth*3/20,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
    		canvas.translate(viewWidth*3/10, 0);
    		patlayout.draw(canvas);
    		patlayout = new StaticLayout(deptName,paint,viewWidth*3/10,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
    		canvas.translate(viewWidth*7/20, 0);
    		patlayout.draw(canvas);
    		patlayout = new StaticLayout(deptLocation,paint,viewWidth*3/10,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
    		canvas.translate(viewWidth*7/20, 0);
    		patlayout.draw(canvas);
    		paint.setTextAlign(Align.CENTER);
        	break;
        case 2:
        	paint.setTextAlign(Align.LEFT);
        	StaticLayout deptlayout = new StaticLayout(deptName,paint,viewWidth*3/5,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
    		canvas.translate(0, 0);
    		deptlayout.draw(canvas);
    		paint.setTextAlign(Align.RIGHT);
    		deptlayout = new StaticLayout(waitNum,paint,viewWidth*2/5,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
    		canvas.translate(viewWidth, 0);
    		deptlayout.draw(canvas);
    		paint.setTextAlign(Align.CENTER);
        	break;
        default:
        	break;
        }
        
        super.onDraw(canvas);  
    }
    
    private void setTextLocation(){
        float textCenterVerticalBaselineY = viewHeight / 2 - textHeight / 2;  
        switch (textAlign) {  
        case TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL:  
            textCenterX = (float)viewWidth / 2;  
            textBaselineY = textCenterVerticalBaselineY;  
            break;  
        case TEXT_ALIGN_LEFT | TEXT_ALIGN_CENTER_VERTICAL:  
            textCenterX = textWidth / 2;  
            textBaselineY = textCenterVerticalBaselineY;  
            break;  
        case TEXT_ALIGN_RIGHT | TEXT_ALIGN_CENTER_VERTICAL:  
            textCenterX = viewWidth - textWidth / 2;  
            textBaselineY = textCenterVerticalBaselineY;  
            break;  
        case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_CENTER_HORIZONTAL:  
            textCenterX = viewWidth / 2;  
            textBaselineY = viewHeight - textHeight;   
            break;  
        case TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL:  
            textCenterX = viewWidth / 2;  
            textBaselineY = 0;  
            break;  
        case TEXT_ALIGN_TOP | TEXT_ALIGN_LEFT:  
            textCenterX = textWidth / 2;  
            textBaselineY = 0;  
            break;  
        case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_LEFT:  
            textCenterX = textWidth / 2;  
            textBaselineY = viewHeight - textHeight;   
            break;  
        case TEXT_ALIGN_TOP | TEXT_ALIGN_RIGHT:  
            textCenterX = viewWidth - textWidth / 2;  
            textBaselineY = 0;  
            break;  
        case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_RIGHT:  
            textCenterX = viewWidth - textWidth / 2;  
            textBaselineY = viewHeight - textHeight;   
            break;  
        }  
    }  
    
    public void setPatient(String patient, String dept, String num, String location){
    	this.patientName = patient;
    	this.deptName = dept;
    	this.visitNum = num;
    	this.deptLocation = location;
    	textType = 1;
        invalidate(); 
    }
    
    public void setDept(String dept, String wait){
    	this.deptName = dept;
    	this.waitNum = wait;
    	textType = 2;
        invalidate(); 
    }
    
    public void setText(String text) {  
        this.text = text;
        textType = 0;
        invalidate();  
    }  

    public void setTextSize(int textSizeSp) {  
        DisplayParams displayParams = DisplayParams.getInstance(context);  
        this.textSize = DisplayUtil.sp2px(textSizeSp, displayParams.fontScale);
        invalidate();  
    }  

    public void setTextAlign(int textAlign) {  
        this.textAlign = textAlign;
        invalidate();  
    }  

    public void setTextColor(int textColor) {  
        this.textColor = textColor;  
        invalidate();  
    }
    
    public void setScroll(int scroll) {
    	stopScroll();
    	scrolling = true;
    	mHandler.sendEmptyMessageDelayed(scroll, 160);
    }
    
    public void stopScroll(){
    	scrolling = false;
    	if (mHandler.hasMessages(0))  
            mHandler.removeMessages(0);
    	if (mHandler.hasMessages(1))  
            mHandler.removeMessages(1);  
    }
}  
