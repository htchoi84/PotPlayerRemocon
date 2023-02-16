package com.htchoi.potplayerremote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

import android.content.Context;
import android.content.Intent;

public class Socket_Thread extends Thread {
	private final String TAG = "Socket_Thread";

	private static DatagramSocket socket = null;

	private static DatagramPacket SendDataPacket = null;

	private static DatagramPacket RecvDataPacket = null;

	byte[] recvMsg = new byte[9];

	private static final int port = 7777;

	private static InetAddress serverAddr;

	private String IP;

	private byte CMD_data[];

	// MainCMD
	private static byte REMOTE_CMD = 0x01;

	private static byte MOUSE_CMD = 0x02;

	private static byte PPTMODE_CMD = 0x03;

	private static byte KEYBOARD_CMD = 0x04;

	private static byte SENSORMOUSE_CMD = 0x05;

	private static byte CONNECT_CMD = 0x06;

	Context mContext;

	public Socket_Thread(String ip_param, Context context) {
		IP = ip_param;
		mContext = context;
	}

	public void stopThread() {
	}

	public void Send_ConnectCMD() {
		Log.i(TAG, "Send_ConnectCMD");
		CMD_data = new byte[9];
		CMD_data[0] = CONNECT_CMD; // MainCMD CONNECT_CMD => 0x06
		CMD_data[1] = 0x00;
		CMD_data[2] = 0x00;
		CMD_data[3] = 0x00;
		CMD_data[4] = 0x00;
		CMD_data[5] = 0x00;
		CMD_data[6] = 0x00;
		CMD_data[7] = 0x00;
		CMD_data[8] = 0x00;

		SendData_Byte();
	}

	public void Send_RemoteCMD(byte subCMD, byte action) {
		Log.i(TAG, "Send_RemoteCMD subCMD : " + subCMD);
		Log.i(TAG, "Send_RemoteCMD action : " + action);
		CMD_data = new byte[9];
		CMD_data[0] = REMOTE_CMD; // MainCMD Remote-CMD => 0x01
		CMD_data[1] = subCMD; // subCMD POWER/OK/UP 등
		CMD_data[2] = action; // action DOWN/UP/MOVE 없으면 0
		CMD_data[3] = 0x00; // X
		CMD_data[4] = 0x00; // X
		CMD_data[5] = 0x00; // Y
		CMD_data[6] = 0x00; // Y
		CMD_data[7] = 0x00; // Z
		CMD_data[8] = 0x00; // Z
		SendData_Byte();
	}

	public void Send_MouseCMD(String subCMD, String action, short data1,
			short data2, short data3) {

		Log.i(TAG, "Send_MouseCMD subCMD : " + subCMD);
		Log.i(TAG, "Send_MouseCMD action : " + action);
		Log.i(TAG, "Send_MouseCMD data1 : " + data1);
		Log.i(TAG, "Send_MouseCMD data2 : " + data2);

		CMD_data = new byte[9];
		CMD_data[0] = MOUSE_CMD; // MainCMD Mouse-CMD => 0x02

		if (subCMD == "TOUCH") {
			CMD_data[1] = MouseFragment.TOUCH; // subCMD POWER/OK/UP 등
			if (action == "DOWN") {
				CMD_data[2] = MouseFragment.DOWN; // action DOWN/UP/MOVE
			} else if (action == "MOVE") {
				CMD_data[2] = MouseFragment.MOVE; // action DOWN/UP/MOVE
			} else if (action == "UP") {
				CMD_data[2] = MouseFragment.UP; // action DOWN/UP/MOVE
			} else
				return;
			shortToByteArray(data1, data2, data3); // x , y, z 데이터를 CMD_data에
													// 저장(z는 사용하지 않음)
		} else if (subCMD == "KEYBOARD") {
			return;
		} else if (subCMD == "LBUTTON") { // 마우스 왼쪽 버튼 이벤트
			CMD_data[1] = MouseFragment.LBUTTON;
			if (action == "DOWN") {
				CMD_data[2] = MouseFragment.DOWN;
			} else if (action == "UP") {
				CMD_data[2] = MouseFragment.UP;
			} else
				return;
			shortToByteArray(data1, data2, data3); // 전달된 키보드입력값 data1을 byte로 변환
		} else if (subCMD == "RBUTTON") { // 마우스 오른쪽 버튼 이벤트
			CMD_data[1] = MouseFragment.RBUTTON;
			if (action == "DOWN") {
				CMD_data[2] = MouseFragment.DOWN;
			} else if (action == "UP") {
				CMD_data[2] = MouseFragment.UP;
			} else
				return;
			shortToByteArray(data1, data2, data3); // 전달된 키보드입력값 data1을 byte로 변환
		} else if (subCMD == "MOUSEWHEEL") {
			CMD_data[1] = MouseFragment.MOUSE_WHEEL; // subCMD
			if (action == "DOWN") {
				CMD_data[2] = MouseFragment.DOWN; // action
			} else if (action == "MOVE") {
				CMD_data[2] = MouseFragment.MOVE; // action
			} else if (action == "UP") {
				CMD_data[2] = MouseFragment.UP; // action
			} else
				return;
			shortToByteArray(data1, data2, data3); // x , y, z 데이터를 CMD_data에
													// 저장(x,z는 사용하지 않음)
		} else {
			return;
		}

		SendData_Byte();
	}

	public void shortToByteArray(short data1, short data2, short data3) {
		// x좌표 short -> byte로 변경 후 저장
		CMD_data[3] = (byte) ((data1 & 0xFF00) >> 8);
		CMD_data[4] = (byte) (data1 & 0x00FF);

		// y좌표 short -> byte로 변경 후 저장
		CMD_data[5] = (byte) ((data2 & 0xFF00) >> 8);
		CMD_data[6] = (byte) (data2 & 0x00FF);

		// z좌표 short -> byte로 변경 후 저장
		CMD_data[7] = (byte) ((data3 & 0xFF00) >> 8);
		CMD_data[8] = (byte) (data3 & 0x00FF);
	}

	public void Send_PPTmodeCMD(String subCMD, String action, short data1,
			short data2, short data3) {

		Log.i(TAG, "Send_SensorMouseCMD subCMD : " + subCMD);
		Log.i(TAG, "Send_SensorMouseCMD action : " + action);
		Log.i(TAG, "Send_SensorMouseCMD data1 : " + data1);
		Log.i(TAG, "Send_SensorMouseCMD data2 : " + data2);

		CMD_data = new byte[9];
		CMD_data[0] = PPTMODE_CMD; // MainCMD SensorMouse-CMD => 0x03
		if (subCMD == "BEGIN_SLIDE") {
			CMD_data[1] = PowerPointFragment.PPTMODE_BEGIN_SLIDE;
			CMD_data[2] = 0x00;
			shortToByteArray((short) 0, (short) 0, (short) 0);
		} else if (subCMD == "CURRENT_SLIDE") {
			CMD_data[1] = PowerPointFragment.PPTMODE_CURRENT_SLIDE;
			CMD_data[2] = 0x00;
			shortToByteArray((short) 0, (short) 0, (short) 0);
		} else if (subCMD == "END_SLIDE") { // 마우스 왼쪽 버튼 이벤트
			CMD_data[1] = PowerPointFragment.PPTMODE_END_SLIDE;
			CMD_data[2] = 0x00;
			shortToByteArray(data1, data2, data3);
		} else if (subCMD == "BEFORE_PAGE") { // 마우스 오른쪽 버튼 이벤트
			CMD_data[1] = PowerPointFragment.PPTMODE_BEFORE_PAGE;
			CMD_data[2] = 0x00;
			shortToByteArray(data1, data2, data3);
		} else if (subCMD == "NEXT_PAGE") {
			CMD_data[1] = PowerPointFragment.PPTMODE_NEXT_PAGE;
			CMD_data[2] = 0x00;
			shortToByteArray(data1, data2, data3);
		} else if (subCMD == "SENSORMOUSE") {
			CMD_data[1] = PowerPointFragment.PPTMODE_SENSORMOUSE;
			CMD_data[2] = 0x00;
			shortToByteArray(data1, data2, data3); // x , y, z 데이터를 CMD_data에
													// 저장(z는 사용하지 않음)
		} else if (subCMD == "LBUTTON") { // 마우스 왼쪽 버튼 이벤트
			CMD_data[1] = PowerPointFragment.PPTMODE_LBUTTON;
			if (action == "DOWN") {
				CMD_data[2] = PowerPointFragment.DOWN;
			} else if (action == "UP") {
				CMD_data[2] = PowerPointFragment.UP;
			} else
				return;
			shortToByteArray(data1, data2, data3); // 전달된 키보드입력값 data1을 byte로 변환
		} else if (subCMD == "RBUTTON") { // 마우스 오른쪽 버튼 이벤트
			CMD_data[1] = PowerPointFragment.PPTMODE_RBUTTON;
			if (action == "DOWN") {
				CMD_data[2] = PowerPointFragment.DOWN;
			} else if (action == "UP") {
				CMD_data[2] = PowerPointFragment.UP;
			} else
				return;
			shortToByteArray(data1, data2, data3); // 전달된 키보드입력값 data1을 byte로 변환
		}

		SendData_Byte();
	}

	public void Send_KeyboardCMD(String action, String key, String status) { // status
		Log.i(TAG, "Send_KeyboardCMD key : " + key);
		Log.i(TAG, "Send_KeyboardCMD action : " + action);

		CMD_data = new byte[9];
		CMD_data[0] = KEYBOARD_CMD; // MainCMD Keyboard-CMD => 0x04
		CMD_data[1] = 0x00;
		if (action == "DOWN") {
			CMD_data[2] = keyboard_Activity.DOWN;
		} else if (action == "UP") {
			CMD_data[2] = keyboard_Activity.UP;
		}

		if (key == "Esc") {
			shortToByteArray((short) 0x1b, (short) 0, (short) 0);
		} else if (key == "F1") {
			shortToByteArray((short) 0x70, (short) 0, (short) 0);
		} else if (key == "F2") {
			shortToByteArray((short) 0x71, (short) 0, (short) 0);
		} else if (key == "F3") {
			shortToByteArray((short) 0x72, (short) 0, (short) 0);
		} else if (key == "F4") {
			shortToByteArray((short) 0x73, (short) 0, (short) 0);
		} else if (key == "F5") {
			shortToByteArray((short) 0x74, (short) 0, (short) 0);
		} else if (key == "F6") {
			shortToByteArray((short) 0x75, (short) 0, (short) 0);
		} else if (key == "F7") {
			shortToByteArray((short) 0x76, (short) 0, (short) 0);
		} else if (key == "F8") {
			shortToByteArray((short) 0x77, (short) 0, (short) 0);
		} else if (key == "F9") {
			shortToByteArray((short) 0x78, (short) 0, (short) 0);
		} else if (key == "F10") {
			shortToByteArray((short) 0x79, (short) 0, (short) 0);
		} else if (key == "F11") {
			shortToByteArray((short) 0x7a, (short) 0, (short) 0);
		} else if (key == "F12") {
			shortToByteArray((short) 0x7b, (short) 0, (short) 0);
		} else if (key == "`") {
			shortToByteArray((short) 0xc0, (short) 0, (short) 0);
		} else if (key == "1") {
			shortToByteArray((short) 0x31, (short) 0, (short) 0);
		} else if (key == "2") {
			shortToByteArray((short) 0x32, (short) 0, (short) 0);
		} else if (key == "3") {
			shortToByteArray((short) 0x33, (short) 0, (short) 0);
		} else if (key == "4") {
			shortToByteArray((short) 0x34, (short) 0, (short) 0);
		} else if (key == "5") {
			shortToByteArray((short) 0x35, (short) 0, (short) 0);
		} else if (key == "6") {
			shortToByteArray((short) 0x36, (short) 0, (short) 0);
		} else if (key == "7") {
			shortToByteArray((short) 0x37, (short) 0, (short) 0);
		} else if (key == "8") {
			shortToByteArray((short) 0x38, (short) 0, (short) 0);
		} else if (key == "9") {
			shortToByteArray((short) 0x39, (short) 0, (short) 0);
		} else if (key == "0") {
			shortToByteArray((short) 0x30, (short) 0, (short) 0);
		} else if (key == "-") {
			shortToByteArray((short) 0xbd, (short) 0, (short) 0);
		} else if (key == "=") {
			shortToByteArray((short) 0xbb, (short) 0, (short) 0);
		} else if (key == "Tab") {
			shortToByteArray((short) 0x9, (short) 0, (short) 0);
		} else if (key == "q") {
			shortToByteArray((short) 0x51, (short) 0, (short) 0);
		} else if (key == "w") {
			shortToByteArray((short) 0x57, (short) 0, (short) 0);
		} else if (key == "e") {
			shortToByteArray((short) 0x45, (short) 0, (short) 0);
		} else if (key == "r") {
			shortToByteArray((short) 0x52, (short) 0, (short) 0);
		} else if (key == "t") {
			shortToByteArray((short) 0x54, (short) 0, (short) 0);
		} else if (key == "y") {
			shortToByteArray((short) 0x59, (short) 0, (short) 0);
		} else if (key == "u") {
			shortToByteArray((short) 0x55, (short) 0, (short) 0);
		} else if (key == "i") {
			shortToByteArray((short) 0x49, (short) 0, (short) 0);
		} else if (key == "o") {
			shortToByteArray((short) 0x4f, (short) 0, (short) 0);
		} else if (key == "p") {
			shortToByteArray((short) 0x50, (short) 0, (short) 0);
		} else if (key == "[") {
			shortToByteArray((short) 0xdb, (short) 0, (short) 0);
		} else if (key == "]") {
			shortToByteArray((short) 0xdd, (short) 0, (short) 0);
		} else if (key == "Cap") {
			if (status.equals("CapsLock"))
				shortToByteArray((short) 0x14, (short) 0x03, (short) 0); // Capslock
			else
				shortToByteArray((short) 0x14, (short) 0x01, (short) 0);
		} else if (key == "a") {
			shortToByteArray((short) 0x41, (short) 0, (short) 0);
		} else if (key == "s") {
			shortToByteArray((short) 0x53, (short) 0, (short) 0);
		} else if (key == "d") {
			shortToByteArray((short) 0x44, (short) 0, (short) 0);
		} else if (key == "f") {
			shortToByteArray((short) 0x46, (short) 0, (short) 0);
		} else if (key == "g") {
			shortToByteArray((short) 0x47, (short) 0, (short) 0);
		} else if (key == "h") {
			shortToByteArray((short) 0x48, (short) 0, (short) 0);
		} else if (key == "j") {
			shortToByteArray((short) 0x4a, (short) 0, (short) 0);
		} else if (key == "k") {
			shortToByteArray((short) 0x4b, (short) 0, (short) 0);
		} else if (key == "l") {
			shortToByteArray((short) 0x4c, (short) 0, (short) 0);
		} else if (key == ";") {
			shortToByteArray((short) 0xba, (short) 0, (short) 0);
		} else if (key == "'") {
			shortToByteArray((short) 0xde, (short) 0, (short) 0);
		} else if (key == "←") {
			shortToByteArray((short) 0x8, (short) 0, (short) 0);
		} else if (key == "Shift") {
			shortToByteArray((short) 0x10, (short) 0, (short) 0);
		} else if (key == "z") {
			shortToByteArray((short) 0x5a, (short) 0, (short) 0);
		} else if (key == "x") {
			shortToByteArray((short) 0x58, (short) 0, (short) 0);
		} else if (key == "c") {
			shortToByteArray((short) 0x43, (short) 0, (short) 0);
		} else if (key == "v") {
			shortToByteArray((short) 0x56, (short) 0, (short) 0);
		} else if (key == "b") {
			shortToByteArray((short) 0x42, (short) 0, (short) 0);
		} else if (key == "n") {
			shortToByteArray((short) 0x4e, (short) 0, (short) 0);
		} else if (key == "m") {
			shortToByteArray((short) 0x4d, (short) 0, (short) 0);
		} else if (key == ",") {
			shortToByteArray((short) 0xbc, (short) 0, (short) 0);
		} else if (key == ".") {
			shortToByteArray((short) 0xbe, (short) 0, (short) 0);
		} else if (key == "/") {
			shortToByteArray((short) 0xbf, (short) 0, (short) 0);
		} else if (key == "\\") {
			shortToByteArray((short) 0xdc, (short) 0, (short) 0);
		} else if (key == "△") {
			shortToByteArray((short) 0x26, (short) 0, (short) 0);
		} else if (key == "Enter") {
			shortToByteArray((short) 0xd, (short) 0, (short) 0);
		} else if (key == "Ctrl") {
			shortToByteArray((short) 0x11, (short) 0, (short) 0);
		} else if (key == "Win") {
			shortToByteArray((short) 0x5c, (short) 0, (short) 0);
		} else if (key == "Alt") {
			shortToByteArray((short) 0x12, (short) 0, (short) 0);
		} else if (key == "space") {
			shortToByteArray((short) 0x20, (short) 0, (short) 0);
		} else if (key == "한/영") {
			if (status.equals("hangul"))
				shortToByteArray((short) 0x15, (short) 0x02, (short) 0);
			else
				shortToByteArray((short) 0x15, (short) 0x01, (short) 0);
		} else if (key == "Del") {
			shortToByteArray((short) 0x2e, (short) 0, (short) 0);
		} else if (key == "◁") {
			shortToByteArray((short) 0x25, (short) 0, (short) 0);
		} else if (key == "▽") {
			shortToByteArray((short) 0x28, (short) 0, (short) 0);
		} else if (key == "▷") {
			shortToByteArray((short) 0x27, (short) 0, (short) 0);
		} else
			return;

		SendData_Byte();
	}

	public void SendData_Byte() {
		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						if (socket == null)
							socket = new DatagramSocket(); // socket 바인딩

						serverAddr = InetAddress.getByName(IP);
						SendDataPacket = new DatagramPacket(CMD_data,
								CMD_data.length, serverAddr, port); // port =
																	// 7777

						socket.send(SendDataPacket); // data 전송

						if (CMD_data[0] == CONNECT_CMD) { // Connect 커맨드를 전송했을때만
							while (true) {
								RecvDataPacket = new DatagramPacket(recvMsg,
										recvMsg.length);
								socket.receive(RecvDataPacket);

								if (recvMsg[0] == CONNECT_CMD) { // connect 정상
									Log.i(TAG, "Server Connect OK");
									Thread.sleep(1500);
									Intent intent = new Intent("com.kycht.android_mouse_project.connect_OK");
									mContext.sendBroadcast(intent);				
									return;
								}
							}
						}
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Close_Socket() {
		Log.i(TAG, "Close_Socket");
		try {
			/*
			 * if(outstream !=null){ outstream.flush(); outstream.close();
			 * socket.close(); }
			 */
			socket.close();
			socket = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
