package com.zierfisch.test;

import java.io.IOException;
import java.util.ArrayList;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

public class ObjImport {
	public static void main(String[] args) {
	    Build builder = new Build();
	    try {
			Parse obj = new Parse(builder, "assets/models/pascal.obj");
			ArrayList<Face> faces = builder.faces;
			System.out.println("Loaded " + builder.objectName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
