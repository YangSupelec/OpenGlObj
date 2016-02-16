    precision mediump float;       	// Set the default precision to medium. We don't need as high of a
                                    // precision in the fragment shader.
    uniform sampler2D u_Texture_Hair;    // The input texture for hair.
    uniform sampler2D u_Texture_Avatar;  // The input texture for avatar.
    uniform int u_Material;              // Distinguer avatar and hair.

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
        float diffuse;
        vec4 decal;

        if (u_Material == 0) {
            // hair, more light absorption
            diffuse = min(0.0,
                          dot(v_Normal, lightVector));
            decal = texture2D(u_Texture_Hair, v_TexCoordinate);
        }
        else {
            // body, more light reflection
            diffuse = max(0.0,
                          dot(v_Normal, lightVector)) - 0.6; // -0.6 reduce lighting
            decal = texture2D(u_Texture_Avatar, v_TexCoordinate);
        }

        diffuse = diffuse / 2.0; // reduce lighting

        gl_FragColor = vec4(decal.rgb.x + diffuse,
                            decal.rgb.y + diffuse,
                            decal.rgb.z + diffuse,
                            decal.a);  // A
    }
