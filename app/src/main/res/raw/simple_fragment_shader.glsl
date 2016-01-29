    precision mediump float;

    varying vec4 v_Color;
    varying vec3 v_Normal;

    // The entry point for our fragment shader.
    void main()
    {
    	// Get a lighting direction vector from the light to the vertex.
        vec3 lightVector = normalize(vec3(0.5, 0.2, 1.0));

    	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
    	// pointing in the same direction then it will get max illumination.
    	float diffuse;

    	if (gl_FrontFacing) {
            diffuse = max(dot(v_Normal, lightVector), 0.0);
        } else {
        	diffuse = max(dot(-v_Normal, lightVector), 0.0);
        }

        // Add ambient lighting
        diffuse = diffuse + 0.3;

    	// Multiply the color by the diffuse illumination level to get final output color.
        gl_FragColor = (v_Color * diffuse);
    }
