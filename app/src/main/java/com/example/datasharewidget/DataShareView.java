package com.example.datasharewidget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.datasharewidget.ProgressButton.OnProgressChangedListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DataShareView extends LinearLayout {
	private static final String TAG = DataShareView.class.getSimpleName();
	private static final boolean DEBUG = true;
	
	private Context mContext;
	private int width;
	private int height;
	private float centerX;
	private float centerY;
	private int min;
	private float padding;
	private float dimeter;
	private float radius;
	private float thickness;
	private RectF innerCircleRect;
	private Paint innerCirclePaint;
	private RectF outerCircleRect;
	private Paint outerCirclePaint;
	private boolean touched;
	private float angle;
	private ProgressButton mView;
	
	public DataShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DataShareView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		Log.d(TAG, "init()");
		mContext = context;
		setWillNotDraw(false);
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		setOrientation(VERTICAL);
		setGravity(Gravity.CENTER);
		inflate(mContext, R.layout.data_share_view, this);
	}	
	
	private void init(){
		this.width = getWidth();
		this.height = getHeight();
		this.centerX = this.width / 2.0F;
		this.centerY = this.height / 2.0F;
		this.min = Math.min(width, height);
		this.padding = Math.max(10.0F, this.min / 7.0F);
		this.dimeter = min - padding;
		this.radius = dimeter / 2.0F;
		this.thickness = Math.max(10.0F, this.min / 50.0F);
		
		outerCircleRect = new RectF();
		outerCircleRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		
		outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		outerCirclePaint.setStyle(Style.FILL);
		
		innerCircleRect = new RectF();
		innerCircleRect.set(centerX - radius + thickness, centerY - radius + thickness, 
				centerX + radius - thickness, centerY + radius - thickness);
		
		innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		innerCirclePaint.setStyle(Style.FILL);
		innerCirclePaint.setColor(Color.WHITE);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.d(TAG, "onFinishedInflate()");
		
		mView = (ProgressButton) findViewById(R.id.button);
		mView.setValue((int) this.angle);
		
		mView.setOnProgressChangeListener(new OnProgressChangedListener() {
			@Override
			public void onProgressChanged(int progress) {
				angle = (float) progress;
				invalidate();
			}
		});
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		init();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d(TAG, "onMeasure()");
		int min = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
		final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
				 (int)(dimeter * 0.4F), MeasureSpec.AT_MOST);
		final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
				 (int)(dimeter * 0.4F), MeasureSpec.AT_MOST);
		
		final View childView = getChildAt(0);
		if(childView != null){
			childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		final View childView = getChildAt(0);
		int left = (int) (centerX - childView.getMeasuredWidth() / 2.0F);
		int top = (int) (centerY - childView.getMeasuredHeight() / 2.0F);
		int right = (int) (centerX + childView.getMeasuredWidth() / 2.0F);
		int bottom = (int) (centerY + childView.getMeasuredHeight() / 2.0F);
		
		if(childView != null){
			childView.layout(left, top, right, bottom);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "onDraw()");
		outerCirclePaint.setColor(Color.LTGRAY);
		canvas.drawArc(outerCircleRect, 0, 360.0F, true, outerCirclePaint);
		innerCirclePaint.setColor(Color.WHITE);
		canvas.drawArc(innerCircleRect, 0, 360.0F, true, innerCirclePaint);
		innerCirclePaint.setColor(0x2F0000FF);
		canvas.drawArc(innerCircleRect, -90, this.angle, true, innerCirclePaint);
		// Draw circle to indicate the touch.
		// Coordinate of a point p for any angle 't' for a circle of radius 'r'=> 
		// p = (px, py) = (r * cos(t), r * sin(t))
		float px = (float) (Math.cos(Math.toRadians(this.angle - 90.0F)) * (radius - thickness / 2.0F));
		float py = (float) (Math.sin(Math.toRadians(this.angle - 90.0F)) * (radius - thickness / 2.0F));
		
		innerCirclePaint.setColor(Color.BLUE);
		canvas.drawCircle(centerX + px, centerY + py, thickness * 3.0F / 2.0F, innerCirclePaint);
		if(this.touched){
			innerCirclePaint.setColor(0x2C0000FF);
			canvas.drawCircle(centerX + px, centerY + py, thickness * 6.0F / 2.0F, innerCirclePaint);
		}
		innerCirclePaint.setColor(Color.WHITE);
		canvas.drawCircle(centerX, centerY, (mView.getWidth() / 2.0F), innerCirclePaint);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		Log.d(TAG, "onInterceptTouchEvent");
		// Return false so that the child view will also receive the touch events
		return false;	
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "onTouchEvent()");
		int action = event.getAction();
		
		float eventX = event.getX();
		float eventY = event.getY();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			this.touched = true;
			break;	
		case MotionEvent.ACTION_UP:
			this.touched = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if(this.touched){
				// Convert the angle returned by Math.atan2() in radian's to degrees.  
				this.angle = (float)Math.toDegrees(Math.atan2(eventY - centerY, eventX - centerX));
				// By default angle is measured from 3'o clocks position, we add 90 so that we can measure it 
				// from 12'o clock position
				this.angle += 90.0F;
				// Resolve the negative angle values. I.e. If We get the angle in degrees as -100,
				// (-100 degrees lies in II quadrant) After adding 90, it is -10. And -10+360 = 350.
				if(this.angle < 0){
					this.angle += 360.0F;
				}
				
				mView.setValue((int)this.angle);
			}		
			break;	
		}
		invalidate();
		return true;
	}
}
