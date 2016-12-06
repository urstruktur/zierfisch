package com.zierfisch.test;

import java.io.IOException;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.parser.Parse;

public class ObjImport {
	public static void main(String[] args) {
	    Build builder = new Build();
	    try {
			Parse obj = new Parse(builder, "assets/models/pascal.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
