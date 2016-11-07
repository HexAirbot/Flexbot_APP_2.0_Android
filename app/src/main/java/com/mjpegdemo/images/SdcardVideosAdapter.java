package com.mjpegdemo.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lewei.lib.LeweiLib;
import com.mjpegdemo.images.MyImageView.OnMeasureListener;
import com.mjpegdemo.images.NativeImageLoader.NativeImageCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cc.flexbot.www.R;

/**
 * adapter for videos
 * 
 * @author Tony
 * 
 */
public class SdcardVideosAdapter extends BaseAdapter
{
	private Point mPoint = new Point(0, 0);// ������װImageView�Ŀ�͸ߵĶ���

	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;

	public SdcardVideosAdapter(Context context, List<String> list, GridView mGridView)
	{
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final ViewHolder viewHolder;
		String path = list.get(position);
		int index = path.lastIndexOf('/');
		String absPath = Environment.getExternalStorageDirectory().toString() + LeweiLib.FOLDER_PATH + path.substring(index); // ���ֻ����ļ��ľ���·��
		// String folderName = new File(path).getParentFile().getName();
		// String folderPath =
		// Environment.getExternalStorageDirectory().toString() + "/" +
		// folderName + "/";
		// String imageName = path.substring(folderPath.length());

		// String imageName = new File(path).getName().replace(".jpg", ".avi");
		// Log.e("", imageName);
		String videoName = new File(path).getName();

		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.hud_view_gallery_item_gridview_videos, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.iv_Videos_Item);
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_Videos_Name_Item);

			// ��������ImageView�Ŀ�͸�
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener()
			{

				@Override
				public void onMeasureSize(int width, int height)
				{
					mPoint.set(width, height);
				}
			});

			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
			viewHolder.mTextView.setText("");
		}
		viewHolder.mImageView.setTag(absPath);

		// ����NativeImageLoader����ر���ͼƬ
		if (videoName.endsWith(".jpg"))
		{
			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(NativeImageLoader.LOAD_TYPE.IMAGE, absPath, mPoint, new NativeImageCallBack()
			{

				@Override
				public void onImageLoader(Bitmap bitmap, String path)
				{
					ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
					if (bitmap != null && mImageView != null)
					{
						mImageView.setImageBitmap(bitmap);
						String imageName = new File(path).getName();
						viewHolder.mTextView.setText(imageName);
					}
				}
			});

			if (bitmap != null)
			{
				viewHolder.mImageView.setImageBitmap(bitmap);
				viewHolder.mTextView.setText(videoName);
			} else
			{
				viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
				viewHolder.mTextView.setText(videoName);
			}
		} else
		{
			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(NativeImageLoader.LOAD_TYPE.VIDEO, absPath, mPoint, new NativeImageCallBack()
			{

				@Override
				public void onImageLoader(Bitmap bitmap, String path)
				{
					ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
					if (bitmap != null && mImageView != null)
					{
						mImageView.setImageBitmap(bitmap);
						String imageName = new File(path).getName();
						viewHolder.mTextView.setText(imageName);
					}
				}
			});

			if (bitmap != null)
			{
				viewHolder.mImageView.setImageBitmap(bitmap);
				viewHolder.mTextView.setText(videoName);
			} else
			{
				viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
				viewHolder.mTextView.setText(videoName);
			}
		}

		return convertView;
	}

	/**
	 * ��ȡѡ�е�Item��position
	 * 
	 * @return
	 */
	public List<Integer> getSelectItems()
	{
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue())
			{
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public static class ViewHolder
	{
		public MyImageView mImageView;
		public TextView mTextView;
	}

}
