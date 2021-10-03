package us.itshighnoon.mirror;

import us.itshighnoon.mirror.lwjgl.Window;

public class Main {
	public static void main(String[] args) {
		Window w = new Window();
		while (!w.shouldClose()) {
			w.poll();
		}
		w.cleanUp();
	}
}
