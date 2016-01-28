package com.example.yang.openglobj.parser;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by yang on 28/01/16.
 */
public class ObjParser {
    protected final String VERTEX = "v";
    protected final String FACE = "f";
    protected final String TEXCOORD = "vt";
    protected final String NORMAL = "vn";

    private File mFile;
    private Resources mResources;
    private int mResourceId;

    public float[] getVertices() {
        return aVertices;
    }

    public float[] getTexCoords() {
        return aTexCoords;
    }

    public float[] getNormals() {
        return aNormals;
    }

    public int[] getIndices() {
        return aIndices;
    }

    private float[] aVertices;
    private float[] aTexCoords;
    private float[] aNormals;
    private float[] aColors;
    private int[] aIndices;

    public ObjParser(File file) {
        mFile = file;
    }

    public ObjParser(Resources resources, int resourceId) {
        mResources = resources;
        mResourceId = resourceId;
    }

    public void parse() {
        BufferedReader buffer = null;
        if (mFile == null) {
            InputStream fileIn = mResources.openRawResource(mResourceId);
            buffer = new BufferedReader(new InputStreamReader(fileIn));
        } else {
            try {
                buffer = new BufferedReader(new FileReader(mFile));
            } catch (FileNotFoundException e) {
                Log.e(getClass().getCanonicalName(), "Could not find file.");
                e.printStackTrace();
            }
        }
        String line;

        ObjIndexData currObjIndexData = new ObjIndexData();
        ArrayList<Float> vertices = new ArrayList<Float>();
        ArrayList<Float> texCoords = new ArrayList<Float>();
        ArrayList<Float> normals = new ArrayList<Float>();

        try {
            while ((line = buffer.readLine()) != null) {
                // Skip comments and empty lines.
                if (line.length() == 0 || line.charAt(0) == '#')
                    continue;
                StringTokenizer parts = new StringTokenizer(line, " ");
                int numTokens = parts.countTokens();

                if (numTokens == 0)
                    continue;
                String type = parts.nextToken();

                if (type.equals(VERTEX)) {
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                } else if (type.equals(FACE)) {
                    boolean isQuad = numTokens == 5;
                    int[] quadvids = new int[4];
                    int[] quadtids = new int[4];
                    int[] quadnids = new int[4];

                    boolean emptyVt = line.indexOf("//") > -1;
                    if (emptyVt) line = line.replace("//", "/");

                    parts = new StringTokenizer(line);

                    parts.nextToken();
                    StringTokenizer subParts = new StringTokenizer(parts.nextToken(), "/");
                    int partLength = subParts.countTokens();

                    boolean hasuv = partLength >= 2 && !emptyVt;
                    boolean hasn = partLength == 3 || (partLength == 2 && emptyVt);
                    int idx;

                    for (int i = 1; i < numTokens; i++) {
                        if (i > 1)
                            subParts = new StringTokenizer(parts.nextToken(), "/");
                        idx = Integer.parseInt(subParts.nextToken());

                        if (idx < 0) idx = (vertices.size() / 3) + idx;
                        else idx -= 1;
                        if (!isQuad)
                            currObjIndexData.vertexIndices.add(idx);
                        else
                            quadvids[i - 1] = idx;
                        if (hasuv) {
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0) idx = (texCoords.size() / 2) + idx;
                            else idx -= 1;
                            if (!isQuad)
                                currObjIndexData.texCoordIndices.add(idx);
                            else
                                quadtids[i - 1] = idx;
                        }
                        if (hasn) {
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0) idx = (normals.size() / 3) + idx;
                            else idx -= 1;
                            if (!isQuad)
                                currObjIndexData.normalIndices.add(idx);
                            else
                                quadnids[i - 1] = idx;
                        }
                    }

                    if (isQuad) {
                        int[] indices = new int[]{0, 1, 2, 0, 2, 3};

                        for (int i = 0; i < 6; ++i) {
                            int index = indices[i];
                            currObjIndexData.vertexIndices.add(quadvids[index]);
                            currObjIndexData.texCoordIndices.add(quadtids[index]);
                            currObjIndexData.normalIndices.add(quadnids[index]);
                        }
                    }
                } else if (type.equals(TEXCOORD)) {
                    texCoords.add(Float.parseFloat(parts.nextToken()));
                    texCoords.add(1f - Float.parseFloat(parts.nextToken()));
                } else if (type.equals(NORMAL)) {
                    try {
                        normals.add(Float.parseFloat(parts.nextToken()));
                        normals.add(Float.parseFloat(parts.nextToken()));
                        normals.add(Float.parseFloat(parts.nextToken()));
                    } catch (NumberFormatException e) { // "-nan" in obj file
                        normals.add(0f);
                        normals.add(0f);
                        normals.add(0f);
                    }
                }
            }
            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        int i;
        aVertices = new float[currObjIndexData.vertexIndices.size() * 3];
        aTexCoords = new float[currObjIndexData.texCoordIndices.size() * 2];
        aNormals = new float[currObjIndexData.normalIndices.size() * 3];
        aColors = new float[currObjIndexData.colorIndices.size() * 4];
        aIndices = new int[currObjIndexData.vertexIndices.size()];

        for (i = 0; i < currObjIndexData.vertexIndices.size(); ++i) {
            int faceIndex = currObjIndexData.vertexIndices.get(i) * 3;
            int vertexIndex = i * 3;
            try {
                aVertices[vertexIndex] = vertices.get(faceIndex);
                aVertices[vertexIndex + 1] = vertices.get(faceIndex + 1);
                aVertices[vertexIndex + 2] = vertices.get(faceIndex + 2);
                aIndices[i] = i;
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            int texCoordIndex = currObjIndexData.texCoordIndices.get(i) * 2;
            int ti = i * 2;
            aTexCoords[ti] = texCoords.get(texCoordIndex);
            aTexCoords[ti + 1] = texCoords.get(texCoordIndex + 1);
        }
        for (i = 0; i < currObjIndexData.colorIndices.size(); ++i) {
            int colorIndex = currObjIndexData.colorIndices.get(i) * 4;
            int ti = i * 4;
            aTexCoords[ti] = texCoords.get(colorIndex);
            aTexCoords[ti + 1] = texCoords.get(colorIndex + 1);
            aTexCoords[ti + 2] = texCoords.get(colorIndex + 2);
            aTexCoords[ti + 3] = texCoords.get(colorIndex + 3);
        }
        for (i = 0; i < currObjIndexData.normalIndices.size(); ++i) {
            int normalIndex = currObjIndexData.normalIndices.get(i) * 3;
            int ni = i * 3;
            aNormals[ni] = normals.get(normalIndex);
            aNormals[ni + 1] = normals.get(normalIndex + 1);
            aNormals[ni + 2] = normals.get(normalIndex + 2);
        }
    }

    protected class ObjIndexData {
        public ArrayList<Integer> vertexIndices;
        public ArrayList<Integer> texCoordIndices;
        public ArrayList<Integer> colorIndices;
        public ArrayList<Integer> normalIndices;

        public ObjIndexData() {
            vertexIndices = new ArrayList<Integer>();
            texCoordIndices = new ArrayList<Integer>();
            colorIndices = new ArrayList<Integer>();
            normalIndices = new ArrayList<Integer>();
        }
    }
}
