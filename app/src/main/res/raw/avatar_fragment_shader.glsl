    precision mediump float;       	// Set the default precision to medium. We don't need as high of a
                                    // precision in the fragment shader.
    uniform sampler2D u_Texture;    // The input texture.

    varying vec3 v_Normal;         	// Interpolated normal for this fragment.
    varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

    // The entry point for our fragment shader.
    void main()
    {
        // Get a lighting direction vector from the light to the vertex.
        vec3 lightVector = vec3(0.5, 0.2, 1.0);
        lightVector = normalize(lightVector);

        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
        // pointing in the same direction then it will get max illumination.
        float diffuse = max(dot(v_Normal, lightVector), 0.0) - 0.6;

        // reduce lighting
        diffuse = diffuse / 2.0;

        vec4 decal = texture2D(u_Texture, v_TexCoordinate);

        // Multiply the color by the diffuse illumination level and texture value to get final output color.
        gl_FragColor = vec4(decal.rgb.x + diffuse,
                            decal.rgb.y + diffuse,
                            decal.rgb.z + diffuse,
                            decal.a);  // A
    }
