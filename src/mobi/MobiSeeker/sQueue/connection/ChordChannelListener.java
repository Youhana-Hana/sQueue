package mobi.MobiSeeker.sQueue.connection;

import java.io.File;

import android.os.StatFs;
import android.util.Log;

import com.samsung.chord.IChordChannelListener;

public class ChordChannelListener implements IChordChannelListener {

	protected static final String MESSAGE_TYPE_FILE_NOTIFICATION = "FILE_NOTIFICATION_V2";

	protected static final long SHARE_FILE_TIMEOUT_MILISECONDS = 1000 * 60 * 5;

	protected IChordServiceListener listener;

	protected FileHandler fileHandler;

	protected ChordManagerService chordManagerService;

	public ChordChannelListener(IChordServiceListener listener,
			FileHandler fileHandler, ChordManagerService chordManagerService) {
		this.listener = listener;

		this.fileHandler = fileHandler;

		this.chordManagerService = chordManagerService;
	}

	/*
	 * Called when a node join event is raised on the channel.
	 * 
	 * @param fromNode The node name corresponding to which the join event has
	 * been raised.
	 * 
	 * @param fromChannel The channel on which the join event has been raised.
	 */
	@Override
	public void onNodeJoined(String fromNode, String fromChannel) {
		Log.v(ConnectionConstant.TAG, ConnectionConstant.TAGClass
				+ "onNodeJoined(), fromNode : " + fromNode + ", fromChannel : "
				+ fromChannel);
		if (null != this.listener)
			this.listener.onNodeEvent(fromNode, fromChannel, true);
	}

	/*
	 * Called when a node leave event is raised on the channel.
	 * 
	 * @param fromNode The node name corresponding to which the leave event has
	 * been raised.
	 * 
	 * @param fromChannel The channel on which the leave event has been raised.
	 */
	@Override
	public void onNodeLeft(String fromNode, String fromChannel) {
		Log.v(ConnectionConstant.TAG, ConnectionConstant.TAGClass
				+ "onNodeLeft(), fromNode : " + fromNode + ", fromChannel : "
				+ fromChannel);
		if (null != this.listener)
			this.listener.onNodeEvent(fromNode, fromChannel, false);
	}

	/*
	 * Called when the data message received from the node.
	 * 
	 * @param fromNode The node name that the message is sent from.
	 * 
	 * @param fromChannel The channel name that is raised event.
	 * 
	 * @param payloadType User defined message type
	 * 
	 * @param payload Array of payload.
	 */
	@Override
	public void onDataReceived(String fromNode, String fromChannel,
			String payloadType, byte[][] payload) {
		Log.v(ConnectionConstant.TAG, ConnectionConstant.TAGClass
				+ "onDataReceived()");
		System.out.println(new String(payload[0]));
		byte[] buf = payload[0];
		if (null != this.listener)
			this.listener.onReceiveMessage(fromNode, fromChannel, new String(
					buf),payloadType);
	}

	/*
	 * Called when the Share file notification is received. User can decide to
	 * receive or reject the file.
	 * 
	 * @param fromNode The node name that the file transfer is requested by.
	 * 
	 * @param fromChannel The channel name that is raised event.
	 * 
	 * @param fileName File name
	 * 
	 * @param hash Hash value
	 * 
	 * @param fileType User defined file type
	 * 
	 * @param exchangeId Exchange ID
	 * 
	 * @param fileSize File size
	 */
	@Override
	public void onFileWillReceive(String fromNode, String fromChannel,
			String fileName, String hash, String fileType, String exchangeId,
			long fileSize) {
		Log.d(ConnectionConstant.TAG, ConnectionConstant.TAGClass
				+ "[originalName : " + fileName + " from : " + fromNode
				+ " exchangeId : " + exchangeId + " fileSize : " + fileSize
				+ "]");

		File targetdir = new File(this.chordManagerService.getChordFilePath());
		if (!targetdir.exists()) {
			targetdir.mkdirs();
		}

		// Because the external storage may be unavailable,
		// you should verify that the volume is available before accessing it.
		// But also, onFileFailed with ERROR_FILE_SEND_FAILED will be called
		// while Chord got failed to write file.
		StatFs stat = new StatFs(this.chordManagerService.getChordFilePath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getAvailableBlocks();
		long availableMemory = blockSize * totalBlocks;

		if (availableMemory < fileSize) {
			this.fileHandler.rejectFile(fromChannel, exchangeId);
			if (null != this.listener)
				this.listener.onFileCompleted(IChordServiceListener.FAILED,
						fromNode, fromChannel, exchangeId, fileName);
			return;
		}

		if (null != this.listener)
			this.listener.onFileWillReceive(fromNode, fromChannel, fileName,
					exchangeId);
	}

	/*
	 * Called when an individual chunk of the file is received. receive or
	 * reject the file.
	 * 
	 * @param fromNode The node name that the file transfer is requested by.
	 * 
	 * @param fromChannel The channel name that is raised event.
	 * 
	 * @param fileName File name
	 * 
	 * @param hash Hash value
	 * 
	 * @param fileType User defined file type
	 * 
	 * @param exchangeId Exchange ID
	 * 
	 * @param fileSize File size
	 * 
	 * @param offset Chunk offset
	 */
	@Override
	public void onFileChunkReceived(String fromNode, String fromChannel,
			String fileName, String hash, String fileType, String exchangeId,
			long fileSize, long offset) {
		if (null != this.listener) {
			int progress = (int) (offset * 100 / fileSize);
			this.listener.onFileProgress(false, fromNode, fromChannel,
					progress, exchangeId);
		}
	}

	/*
	 * Called when the file transfer is completed from the node.
	 * 
	 * @param fromNode The node name that the file transfer is requested by.
	 * 
	 * @param fromChannel The channel name that is raised event.
	 * 
	 * @param fileName File name
	 * 
	 * @param hash Hash value
	 * 
	 * @param fileType User defined file type
	 * 
	 * @param exchangeId Exchange ID
	 * 
	 * @param fileSize File size
	 * 
	 * @param tmpFilePath Temporarily stored file path it is assigned with
	 * setTempDirectory()
	 */
	@Override
	public void onFileReceived(String fromNode, String fromChannel,
			String fileName, String hash, String fileType, String exchangeId,
			long fileSize, String tmpFilePath) {

		String savedName = fileName;

		int i = savedName.lastIndexOf(".");
		String name = savedName.substring(0, i);
		String ext = savedName.substring(i);
		Log.d(ConnectionConstant.TAG, ConnectionConstant.TAGClass
				+ "onFileReceived : " + fileName);
		Log.d(ConnectionConstant.TAG, ConnectionConstant.TAGClass
				+ "onFileReceived : " + name + " " + ext);

		File targetFile = new File(this.chordManagerService.getChordFilePath(),
				savedName);
		int index = 0;
		while (targetFile.exists()) {
			savedName = name + "_" + index + ext;
			targetFile = new File(this.chordManagerService.getChordFilePath(),
					savedName);

			index++;

			Log.d(ConnectionConstant.TAG, ConnectionConstant.TAGClass
					+ "onFileReceived : " + savedName);
		}

		File srcFile = new File(tmpFilePath);
		srcFile.renameTo(targetFile);

		if (null != this.listener) {
			this.listener.onFileCompleted(IChordServiceListener.RECEIVED,
					fromNode, fromChannel, exchangeId, savedName);
		}

	}

	/*
	 * Called when an individual chunk of the file is sent.
	 * 
	 * @param toNode The node name to which the file is sent.
	 * 
	 * @param toChannel The channel name that is raised event.
	 * 
	 * @param fileName File name
	 * 
	 * @param hash Hash value
	 * 
	 * @param fileType User defined file type
	 * 
	 * @param exchangeId Exchange ID
	 * 
	 * @param fileSize File size
	 * 
	 * @param offset Offset
	 * 
	 * @param chunkSize Chunk size
	 */
	@Override
	public void onFileChunkSent(String toNode, String toChannel,
			String fileName, String hash, String fileType, String exchangeId,
			long fileSize, long offset, long chunkSize) {

		if (null != this.listener) {
			int progress = (int) (offset * 100 / fileSize);
			this.listener.onFileProgress(true, toNode, toChannel, progress,
					exchangeId);
		}
	}

	/*
	 * Called when the file transfer is completed to the node.
	 * 
	 * @param toNode The node name to which the file is sent.
	 * 
	 * @param toChannel The channel name that is raised event.
	 * 
	 * @param fileName File name
	 * 
	 * @param hash Hash value
	 * 
	 * @param fileType User defined file type
	 * 
	 * @param exchangeId Exchange ID
	 */
	@Override
	public void onFileSent(String toNode, String toChannel, String fileName,
			String hash, String fileType, String exchangeId) {
		if (null != this.listener) {
			this.listener.onFileCompleted(IChordServiceListener.SENT, toNode,
					toChannel, exchangeId, fileName);
		}
	}

	/*
	 * Called when the error is occurred while the file transfer is in progress.
	 * 
	 * @param node The node name that the error is occurred by.
	 * 
	 * @param channel The channel name that is raised event.
	 * 
	 * @param fileName File name
	 * 
	 * @param hash Hash value
	 * 
	 * @param exchangeId Exchange ID
	 * 
	 * @param reason The reason for failure could be one of #ERROR_FILE_CANCELED
	 * #ERROR_FILE_CREATE_FAILED #ERROR_FILE_NO_RESOURCE #ERROR_FILE_REJECTED
	 * #ERROR_FILE_SEND_FAILED #ERROR_FILE_TIMEOUT
	 */
	@Override
	public void onFileFailed(String node, String channel, String fileName,
			String hash, String exchangeId, int reason) {
		switch (reason) {
		case ERROR_FILE_REJECTED: {
			Log.e(ConnectionConstant.TAG, ConnectionConstant.TAGClass
					+ "onFileFailed() - REJECTED Node : " + node
					+ ", fromChannel : " + channel);
			if (null != this.listener) {
				this.listener.onFileCompleted(IChordServiceListener.REJECTED,
						node, channel, exchangeId, fileName);
			}
			break;
		}

		case ERROR_FILE_CANCELED: {
			Log.e(ConnectionConstant.TAG, ConnectionConstant.TAGClass
					+ "onFileFailed() - CANCELED Node : " + node
					+ ", fromChannel : " + channel);
			if (null != this.listener) {
				this.listener.onFileCompleted(IChordServiceListener.CANCELLED,
						node, channel, exchangeId, fileName);
			}
			break;
		}
		case ERROR_FILE_CREATE_FAILED:
		case ERROR_FILE_NO_RESOURCE:
		default:
			Log.e(ConnectionConstant.TAG, ConnectionConstant.TAGClass
					+ "onFileFailed() - " + reason + " : " + node
					+ ", fromChannel : " + channel);
			if (null != this.listener) {
				this.listener.onFileCompleted(IChordServiceListener.FAILED,
						node, channel, exchangeId, fileName);
			}
			break;
		}

	}
};
