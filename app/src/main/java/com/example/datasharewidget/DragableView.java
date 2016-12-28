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
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DragableView extends View implements Dragable {
	private static final boolean DEBUG = true; 
	private static final String TAG = DragableView.class.getSimpleName();
	
	private int value;
	private boolean mDragInProgress;
	private boolean mAcceptsDrag;
	private boolean mHovering;
	private int width;
	private int height;
	private float centerX;
	private float centerY;
	private float radius;
	private Paint paint;
	private Paint textPaint;
	private int min;
	private float padding;
	private float size;

	public DragableView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DragableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragableView(Context context) {
		super(context);
		init();
	}

	private void init() {
		this.width = getWidth();
		this.height = getHeight();
		this.centerX = width / 2.0F;
		this.centerY = height / 2.0F;
		this.min = Math.min(width, height);
		this.padding = Math.min(10.0F, min / 15.0F);
		this.size = min - padding;
		this.radius = size / 2.0F;
		
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
				startDrag(clipData, new DragShadowBuilder(DragableView.this), v, 0);
				return true;
			}
		});
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.value = Integer.parseInt((String)getTag());
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
	}

	@Override
	public int getValue() {
		return this.value;
	}
	
	@Override
	public boolean onDragEvent(DragEvent event) {
		boolean result = false;
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
			// claim to accept any dragged content
            Log.i(TAG, "Drag started " + getContentDescription());
            // cache whether we accept the drag to return for LOCATION events
            mDragInProgress = true;
            mAcceptsDrag = result = true;
            // Redraw in the new visual state if we are a potential drop target
            if (mAcceptsDrag) {
                invalidate();
            }
			break;
		case DragEvent.ACTION_DRAG_ENDED:
			Log.i(TAG, "Drag ended. " + getContentDescription());
            if (mAcceptsDrag) {
                invalidate();
            }
            mDragInProgress = false;
            mHovering = false;
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			Log.i(TAG, "Entered dot " + getContentDescription());
            mHovering = true;
            invalidate();
			break;
		case DragEvent.ACTION_DROP:
			Log.i(TAG, "Got a drop! dot : " + getContentDescription());
            processDrop(event);
            result = true;
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			Log.i(TAG, "Exited dot " + getContentDescription());
            mHovering = false;
            invalidate();
			break;
		case DragEvent.ACTION_DRAG_LOCATION:
			// we returned true to DRAG_STARTED, so return true here
            Log.i(TAG, "Seeing drag locations..." + getContentDescription());
            result = mAcceptsDrag;
			break;
		default:
			Log.i(TAG, "Other drag event: " + event);
            result = mAcceptsDrag;
			break;
		}
		return result;
	}

	private void processDrop(DragEvent event) {
		final ClipData clipData = event.getClipData();
		final int n = clipData.getItemCount();
		for (int i = 0; i < n; i++) {
			ClipData.Item item = clipData.getItemAt(i);		
			int currentValue = Integer.parseInt(item.coerceToText(getContext()).toString());
            if (event.getLocalState() == (Object) this) {
                Log.d(TAG, "Dropped on self!");
            }else{
            	Object view = event.getLocalState();
            	if(view instanceof Dragable){
            		Dragable sourceView = (Dragable) view;
            		sourceView.setValue(sourceView.getValue() - currentValue);
            		setValue(currentValue + getValue());
            	}
            }
		}
	}
	
	@Override
	public String toString() {
		return getContentDescription().toString();
	}
}
