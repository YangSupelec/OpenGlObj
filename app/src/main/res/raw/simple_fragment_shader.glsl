    precision mediump float;

    varying vec3 v_Normal;         // Interpolated normal for this fragment.
    varying vec3 v_Position;       // Interpolated position for this fragment.
    varying vec4 v_Color;          // This is the color from the vertex shader interpolated across the
                                   // triangle per fragment.

    void main() {
        // Get a lighting direction vector from the light to the vertex.
        vec3 lightVector = normalize( - v_Position);

        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
        // pointing in the same direction then it will get max illumination.
        float diffuse = max(dot(v_Normal, lightVector), 0.1);
        gl_FragColor = v_Color * diffuse;
    }
