#version 330

/** Rendered image in (unclamped) HDR */
uniform sampler2D hdr;
/** Rolling average luminosity */
uniform float avgLuminosity;
/** Kalled key in slides, scaling factor for brightness */
const float exposure = 0.08;

in vec2 st;

out vec4 color;

float luminosity(vec3 color) {
    return 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
}

/*float avgLuminosity(sampler2D tex) {
    const int steps = 4;
    const float step = 1.0 / float(steps);

    vec4 colorSum = vec4(0.0, 0.0, 0.0, 0.0);
    for(float x = 0.0; x < 1.0; x += step) {
        for(float y = 0.0; y < 1.0; y += step) {
            colorSum += texture(hdr, vec2(x, y));
        }
    }

    return luminosity(colorSum.rgb / float(steps*steps));
}*/

vec4 hdrToClampedRgb() {
    vec4 hdrColor = texture(hdr, st);
    float thisL = luminosity(hdrColor.rgb);
    float avgL = avgLuminosity;
    float scaledL = (exposure * thisL) / avgL;

    //vec4 color = scaledL * (1 + (scaledL / (thisL*thisL))) / (1 + scaledL);
    vec4 color = (scaledL * (1 + (scaledL / (luminosity(hdrColor.rgb)*luminosity(hdrColor.rgb)))) / (1 + scaledL)) * hdrColor;
    color = clamp(color, 0.0, 1.0);
    return color;
}

void main() {
    color = hdrToClampedRgb();
    color.a = 1.0;

    //color.rgb = fract(vec3(avgLuminosity, avgLuminosity, avgLuminosity));

    //float gamma = 1.0/2.2;
    //color.rgb = pow(color.rgb, vec3(1.0/gamma, 1.0/gamma, 1.0/gamma));
}
