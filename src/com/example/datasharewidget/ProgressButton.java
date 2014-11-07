package com.example.datasharewidget;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ProgressButton extends View implements Dragable{

	private int width;
	private int height;
	private float centerX;
	private float centerY;
	private float radius;
	private Paint paint;
	private Paint textPaint;
	private int value;
	private OnProgressChangedListener mListener;
	
	public interface OnProgressChangedListener{
		public void onProgressChanged(int progress);
	}
	
	public void setOnProgressChangeListener(OnProgressChangedListener listener){
		this.mListener = listener;
	}
	
	public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ProgressButton(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		this.width = getWidth();
		this.height = getHeight();
		this.centerX = width / 2.0F;
		this.centerY = height / 2.0F;
		this.radius = Math.min(width, height) / 2.0F;
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Style.FILL);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize((radius * 2) / 3.0F);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
		
		setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ClipData clipData = ClipData.newPlainText("value", String.valueOf(value));
				startDrag(clipData, new DragShadowBuilder(ProgressButton.this), v, 0);
				return true;
			}
		});
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(centerX, centerY, radius, paint);
		String text = String.valueOf(value);
		canvas.drawText(text, centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2.0F, textPaint);
	}

	@Override
	public void setValue(int progress) {
		this.value = progress;
		invalidate();
		if(mListener != null){
			mListener.onProgressChanged(progress);
		}
	}

	@Override
	public int getValue() {
		return this.value;
	}
}
