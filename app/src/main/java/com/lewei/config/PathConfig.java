package com.lewei.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images;
import android.util.Log;

public class PathConfig
{
	private Context context;
	private SwitchConfig mSwitchConfig;
	public static SdcardSelector sdcardItem = SdcardSelector.BUILT_IN;
	
	public static final String PHOTOS_PATH = "/H264Demo/Photos";
	public static final String VIDEOS_PATH = "/H264Demo/Videos";
	private final static String PARENTFOLDER = "H264Demo";
	private final static String PHOTOS = "Photos";
	private final static String VIDEOS = "Videos";

	private List<String> videoList = new ArrayList<String>();

	public static enum SdcardSelector
	{
		BUILT_IN, EXTERNAL
	}

	public PathConfig(Context context)
	{
		// TODO Auto-generated constructor stub
		this.context = context;
		mSwitchConfig = new SwitchConfig(context);
		if(mSwitchConfig.readSdcardChoose())
			sdcardItem = SdcardSelector.EXTERNAL;
		else 
			sdcardItem = SdcardSelector.BUILT_IN;
	}

	public void setSdcardItem(SdcardSelector item)
	{
		sdcardItem = item;
	}

	/**
	 * return video path, if the video is not exist, then create it
	 * 
	 * @param parentFolder
	 *            like:DCIM/VIDEO
	 * @param videoName
	 *            like:VIDEO1.AVI
	 * @return
	 */
	public String getVideoPath(String parentFolder, String videoName)
	{
		String absolutePath = null;
		try
		{
			String sdCardDir;
			if (sdcardItem == SdcardSelector.BUILT_IN)
			{
				sdCardDir = Environment.getExternalStorageDirectory().toString();
			} else
			{
				sdCardDir = Environment.getExternalStorageDirectory().toString();
				if (sdCardDir == null)
					return null;
			}
			String videoPath = sdCardDir + "/" + parentFolder + "/";
			File folder = new File(videoPath);
			if (!folder.exists())
			{
				folder.mkdirs();
			}
			File saveVideo = new File(videoPath + videoName);
			if (!saveVideo.exists())
			{
				saveVideo.createNewFile();
			}
			absolutePath = saveVideo.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return absolutePath;
	}
	
	/**
	 * get video name without any params
	 * @return video path
	 */
	public String getVideoPath()
	{
		String absolutePath = null;
		try
		{
			String sdCardDir;
			if (sdcardItem == SdcardSelector.BUILT_IN)
			{
				sdCardDir = Environment.getExternalStorageDirectory().toString();
			} else
			{
				sdCardDir = Environment.getExternalStorageDirectory().toString();
				if (sdCardDir == null)
					return null;
			}
			String videoPath = sdCardDir + "/" + PARENTFOLDER + "/" + VIDEOS + "/";
			File folder = new File(videoPath);
			if (!folder.exists())
			{
				folder.mkdirs();
			}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
			long time = System.currentTimeMillis();
			Date curDate = new Date(time);
			String timeString = format.format(curDate);
			
			File saveVideo = new File(videoPath + timeString + ".mp4");
			if (!saveVideo.exists())
			{
				saveVideo.createNewFile();
			}
			absolutePath = saveVideo.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return absolutePath;
	}

	/**
	 * return the sdcard path
	 * 
	 * @return
	 */
	public String getRootPath()
	{
		String sdCardDir;
		if (sdcardItem == SdcardSelector.BUILT_IN)
		{
			sdCardDir = Environment.getExternalStorageDirectory().toString();
		} else
		{
			sdCardDir = Environment.getExternalStorageDirectory().toString();
			if (sdCardDir == null)
				return null;
		}

		return sdCardDir;
	}

	/**
	 * save photos use bytes stream
	 * 
	 * @param parentFolder
	 *            like:Photo
	 * @param photoName
	 *            like:IMAGE1.JPG
	 * @param imagedata
	 *            image bytes stream data
	 */

	public void savePhoto(String parentFolder, String photoName, byte[] imagedata)
	{
		String sdCardDir = getRootPath();
		if (sdCardDir != null)
		{
			try
			{
				String photoPath = sdCardDir + "/" + parentFolder + "/";
				File folder = new File(photoPath);
				if (!folder.exists())
				{
					folder.mkdirs();
				}
				File savePhoto = new File(photoPath, photoName);
				if (!savePhoto.exists())
				{
					savePhoto.createNewFile();
				}
				String absolutePath = savePhoto.getAbsolutePath();
				Log.e("path", absolutePath);

				FileOutputStream fout;

				fout = new FileOutputStream(absolutePath);

				fout.write(imagedata, 0, imagedata.length);
				fout.close();

			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	/**
	 * save photos use bitmap
	 * @param parentFolder
	 * @param photoName
	 * @param bmp bitmap data
	 */
	public void savePhoto(Bitmap bmp)
	{
		String sdCardDir = getRootPath();
		if (sdCardDir != null)
		{
			try
			{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
				long time = System.currentTimeMillis();
				Date curDate = new Date(time);
				String timeString = format.format(curDate);
				
				String photoPath = sdCardDir + "/" + PARENTFOLDER + "/" + PHOTOS;
				File folder = new File(photoPath);
				if (!folder.exists())
				{
					folder.mkdirs();
				}
				String photoName = timeString + ".jpg";
				File savePhoto = new File(photoPath, photoName);
				if (!savePhoto.exists())
				{
					savePhoto.createNewFile();
				}
				String absolutePath = savePhoto.getAbsolutePath();
				Log.e("path", absolutePath);

				FileOutputStream fout;

				fout = new FileOutputStream(absolutePath);

				bmp.compress(CompressFormat.JPEG, 80, fout);
				
				fout.close();
			
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri uri = path2uri(Uri.fromFile(new File(photoPath + photoName)));
				Log.e("Display Activity", "uri  " + uri.toString());
				intent.setData(uri);
				context.sendBroadcast(intent);
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	public List<String> getImagesList(final File photoPath)
	{
		List<String> photoList = new ArrayList<String>();

		FileFilter filter = new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				if (file.isFile()
						&& (file.getAbsolutePath().toLowerCase().endsWith(".bmp") || file.getAbsolutePath().toLowerCase().endsWith(".jpg") || file.getAbsolutePath().toLowerCase().endsWith(".png")))
				{
					return true;
				} else
					return false;
			}
		};

		File[] filterFiles = photoPath.listFiles(filter);
		if (null != filterFiles && filterFiles.length > 0)
		{

			for (File file : filterFiles)
			{
				// ��߶��ļ����й���
				if (photoList.indexOf(file.getAbsolutePath()) == -1)
				{
					// Log.e(Tag, file.getAbsolutePath());
					photoList.add(file.getAbsolutePath());
				}

			}
		}
		return photoList;
	}

	public List<String> getVideosList(final File videoPath)
	{
		videoList.clear();
		//getVideoList(videoPath);
		getVideoListNew(videoPath);
		return videoList;
	}
	
	/**
	 * New Method:�·�������Ҫ��¼��ǰ��������Ϊ����ͼ
	 * @param videoPath
	 */
	private void getVideoListNew(final File videoPath)
	{
		List<String> temp = new ArrayList<String>();
		File[] files = videoPath.listFiles();
		if (files != null && files.length > 0)
		{
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile())
				{
					if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi") || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp")
							|| files[i].getAbsolutePath().toLowerCase().endsWith(".mp4"))
					{
						String absPath = files[i].getAbsolutePath();
						
						File videoFile = new File(absPath);
						if (videoFile.exists())
						{
							if (temp.indexOf(videoFile.getAbsolutePath()) == -1)
							{
								temp.add(videoFile.getAbsolutePath());
								videoList.add(videoFile.toString());
							}
						}
					}

				} else
				{
					if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
					{
						getVideoList(files[i]);
					}
				}
			}
		}
	}

	/**
	 * Old Method:�Ϸ�������¼��ǰ������Ϊ��Ƶ����ͼ�������Ļ�Ҫ����Ƶ��ַ�滻Ϊ����Ƭ��ַ����ʾ
	 * @param videoPath
	 */
	private void getVideoList(final File videoPath)
	{
		List<String> temp = new ArrayList<String>();
		File[] files = videoPath.listFiles();
		if (files != null && files.length > 0)
		{
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile())
				{
					if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi") || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp")
							|| files[i].getAbsolutePath().toLowerCase().endsWith(".mp4"))
					{
						String lcPath = files[i].getAbsolutePath().toLowerCase();
						String absPath = files[i].getAbsolutePath();
						String photopath = null;
						if (lcPath.contains(".avi"))
						{
							photopath = absPath.replace(".avi", ".jpg");
						} else if (lcPath.contains(".mp4"))
						{
							photopath = absPath.replace(".mp4", ".jpg");
						} else if (lcPath.contains(".3gp"))
						{
							photopath = absPath.replace(".3gp", ".jpg");
						}
						File photofile = new File(photopath);
						if (photofile.exists())
						{
							if (temp.indexOf(photofile.getAbsolutePath()) == -1)
							{
								temp.add(photofile.getAbsolutePath());
								videoList.add(photofile.toString());
							}
						}
					}

				} else
				{
					if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
					{
						getVideoList(files[i]);
					}
				}
			}
		}
	}

	private Uri path2uri(Uri uri)
	{
		if (uri.getScheme().equals("file"))
		{
			String path = uri.getEncodedPath();
			// Log.d("", "path1 is " + path);
			if (path != null)
			{
				path = Uri.decode(path);
				// Log.d("", "path2 is " + path);
				ContentResolver cr = context.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID }, buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext())
				{
					index = cur.getColumnIndex(Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0)
				{
					// do nothing
				} else
				{
					Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
					Log.d("", "uri_temp is " + uri_temp);
					if (uri_temp != null)
					{
						uri = uri_temp;
					}
				}
			}
		}
		return uri;
	}

	public int getSdcardAvilibleSize()
	{
		String sdCardDir = getRootPath();
		StatFs stat = new StatFs(new File(sdCardDir).getPath());
		/* ��ȡblock��SIZE */
		long blockSize = stat.getBlockSize();
		/* ���е�Block������ */
		long availableBlocks = stat.getAvailableBlocks();
		/* ����bit��Сֵ */
		return (int) (availableBlocks * blockSize / 1024 / 1024);
	}

	public int getSdcardTotalSize()
	{
		String sdCardDir = getRootPath();
		StatFs stat = new StatFs(new File(sdCardDir).getPath());
		/* ��ȡblock��SIZE */
		long blockSize = stat.getBlockSize();
		/* ���е�Block������ */
		long blockCount = stat.getBlockCount();
		/* ����bit��Сֵ */
		return (int) (blockCount * blockSize / 1024 / 1024);
	}

	/** ��¼���޸�ʱ����Ⱥ����� */
	public List<File> sortVideoList(List<File> photoList)
	{
		Collections.sort(photoList, new Comparator<File>()
		{

			@Override
			public int compare(File curFile, File nextFile)
			{
				// TODO Auto-generated method stub
				long firstDate = curFile.lastModified();
				long nextDate = nextFile.lastModified();
				return (firstDate > nextDate) ? 1 : -1; // �����ڣ������޸ģ�����1�������޸�ʱ��˳��
			}
		});
		return photoList;
	}

	/** delete all the files in the folder and it's sub folders */
	public void deleteFiles(File file)
	{
		if (file.exists())
		{
			if (file.isFile())
			{
				file.delete();
			} else if (file.isDirectory())
			{
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					this.deleteFiles(files[i]);
				}
			}
			file.delete();
		}
	}
}
