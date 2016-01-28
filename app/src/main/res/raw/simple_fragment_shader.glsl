    precision mediump float;

    uniform sampler2D u_Texture;

    varying vec3 v_Normal;
    varying vec2 v_TexCoordinate;
    varying vec4 v_Color;

    void main() {
        // Get a lighting direction vector from the light to the vertex.
        vec3 light = vec3(0.5, 0.2, 1.0);

        light = normalize(light);

        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
        // pointing in the same direction then it will get max illumination.
        float diffuse = max(dot(v_Normal, light), 0.1);
        // Add ambient lighting
        diffuse = diffuse + 0.3;
        gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate));
    }
