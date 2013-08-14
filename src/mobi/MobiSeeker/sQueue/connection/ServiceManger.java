package mobi.MobiSeeker.sQueue.connection;

import java.util.ArrayList;
import java.util.List;

import mobi.MobiSeeker.sQueue.activites.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import mobi.MobiSeeker.sQueue.connection.ChordApiService.ChordServiceBinder;
import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;

public class ServiceManger {
	
	 private static ServiceManger servicemanger;
	private ChordApiService mChordService;
	BaseActivity mainActivity;
	List<IChordChannel> listOfChannels;
	private boolean Connected ;
	
	private ArrayList<CashedNodesData> cashedNotes;
	
	static onConnected onconnected;
	
	private ServiceManger(BaseActivity activity)
	{
		this.mainActivity=activity;
		cashedNotes=new ArrayList<CashedNodesData>();
	}	
	
	public static ServiceManger getInstance(BaseActivity activity,boolean reCreate,onConnected onConnected)
	{
		if(servicemanger==null)
		{
		servicemanger=new ServiceManger(activity);
		if(onConnected!=null)
		onconnected=onConnected;	
		servicemanger.Connect();
		
		}else
		{
			if(!servicemanger.isConnected())
			{
				servicemanger.Connect();
			}
		}
		return servicemanger;
	}

	private ServiceConnection serviceConnection;
	
	public void Connect()
	{
		serviceConnection=new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				mChordService=null;
				Connected=false;
				
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				ChordServiceBinder binder=(ChordServiceBinder)service;
				mChordService=binder.getService();
				try
				{
					mChordService.initialize(mainActivity);
					Connected=true;
				
				}catch(Exception EE)
				{
					EE.printStackTrace();
				}
				
				mChordService.start(ChordManager.INTERFACE_TYPE_WIFI);
				mChordService.joinChannel(NodeManager.CHORD_API_CHANNEL);
				listOfChannels=mChordService.getJoinedChannelList();
				if(onconnected!=null)
					onconnected.connected();
			
				
				System.out.println(listOfChannels);
				for(int i=0;i<cashedNotes.size();i++)
				{
					sendData(cashedNotes.get(i).message, cashedNotes.get(i).messageType, cashedNotes.get(i).nodeName);
				}
				cashedNotes.clear();
				sendDataToAll("", ConnectionConstant.GET_DEVICE_NAME);
			//	sendDataToAll("welcome",ConnectionConstant.SEND_MESSAGE);
			}
		};
		
	}
	
	

	
	public void reConnect()
	{
		if(!isConnected())
		{
			Connect();
		}
		
	}
	
	
	public boolean isConnected() {
		return Connected;
	}


	public void setConnected(boolean connected) {
		Connected = connected;
	}


	public ChordApiService getmChordService() {
		return mChordService;
	}


	public void setmChordService(ChordApiService mChordService) {
		this.mChordService = mChordService;
	}


	public void sendData(String data,String messageType,String nodeName)
	{
		if(mChordService!=null)
		{			
			if(isConnected()){
			mChordService.sendData(NodeManager.CHORD_API_CHANNEL, data.getBytes(), nodeName, messageType);
			}else
			{
			CashedNodesData cashedNote=new CashedNodesData();
			cashedNote.nodeName=nodeName;
			cashedNote.message=data;
			cashedNote.messageType=messageType;
			cashedNotes.add(cashedNote);	
			}
		}
		
	}
	
	
	

	public List<IChordChannel> getListOfChannels() {
		return listOfChannels;
	}


	public void setListOfChannels(List<IChordChannel> listOfChannels) {
		this.listOfChannels = listOfChannels;
	}


	public void sendDataToAll(String data,String messageType)
	{
		if(mChordService!=null)
		{
			mChordService.sendDataToAll(NodeManager.CHORD_API_CHANNEL, data.getBytes(),messageType);
		}
		
	}
	
	 public void bindChordService()
	 {
	        if (mChordService == null) {
	            Intent intent = new Intent("mobi.MobiSeeker.sQueue.connection.ChordApiService.SERVICE_BIND");
	            mainActivity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	        }
    }

	 public void startService()
	 {
	        Intent intent = new Intent("mobi.MobiSeeker.sQueue.connection.ChordApiService.SERVICE_START");
	        mainActivity.startService(intent);
	 }

	 private void stopService()
	 {
	        Intent intent = new Intent("mobi.MobiSeeker.sQueue.connection.ChordApiService.SERVICE_STOP");
	        mainActivity.stopService(intent);
	 }

	 
	public void initalize()
	{
		
		
	}
	

}


class CashedNodesData
{
	public String nodeName;
	public String messageType;
	public String message;


}
