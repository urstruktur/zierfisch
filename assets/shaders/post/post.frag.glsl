#version 330

/** Rendered image in (unclamped) HDR */
uniform sampler2D hdr;
uniform sampler2D texture1;
uniform float avgLuminosity;
uniform float rollingAvgLuminosity;
/** Rolling average color */
uniform vec4 avgColor;
uniform vec4 rollingAvgColor;

/** HDR key for scaled luminance */
const float keyBase = 0.03;

const float exposure = 10.0;

/** Maximum upwards exposure deviation when adapting to bright color */
const float maxOverexposure = exposure * 2;
/** Maximum downwards exposure deviation when adapting to dark color */
const float maxUnderexposure = exposure;
/** The higher this value, the more over- or underexposure effects will be shown */
const float adaptionSlowdown = 10.0;

in vec2 st;

out vec4 color;

float luminosity(vec4 color) {
    return 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
}

///
/// Takes a color in high dynamic range and transforms it
/// into SRGB color space. That is, HDR colors are mapped to the
/// interval 0 ≤ x ≤ 1. Note that no gamma correction is applied
/// to the resulting vector. This is better left to the hardware
/// by writing into a framebuffer with SRGB enabled.
///
/// @param exposure
///            Scales the overall brightness of the resulting image
/// @param color
///            The color in high dynamic range
/// @param avgL
///            The average color of the HDR image the color was obtained from
///
vec4 hdrToClampedSRGB(float key, float exposure, vec4 color, vec4 avgColor) {
    // Average luminosity
    float avgL = luminosity(avgColor);
    // Luminosity of this fragment
    float fragL = luminosity(color);
    // Scaled luminosity with respect to the average luminosity
    float scaledL = (key * fragL) / avgL;

    // Good ol' Reinhard, no modifications
    // float mappedL = scaledL / (1.0 + scaledL);

    // Reinhard's operator for tone mapping, modified version
    float mappedL =  ( scaledL * (1.0 + (scaledL / (fragL*fragL))) ) /
                     ( 1.0 + scaledL );

    // Exposure tone mapping @see https://learnopengl.com/#!Advanced-Lighting/HDR
    vec3 ldr = vec3(1.0) - exp(-color.rgb * scaledL * exposure);

    // Scale by mapped luminosity and clamp to 0 ≤ x ≤ 1
    // Alpha is set to 1.0
    return vec4(
        clamp(ldr, 0.0, 1.0),
        1.0
    );
}

///
/// Chooses a HDR key that simulates the eyes tendency to take
/// some time to copensate for luminosity fluctuations.
///
/// The key is calculated from the deviation of a given average
/// color from a given rolling average color. The rolling average
/// color should be calculated from the average color of the last
/// n frames, where n can be chosen to tweak eye adaption speed.
///
/// @param avgColor
///            Average color of the HDR image that will be rendered this frame
/// @param rollingAvgColor
///            Average average color over the last n frames
///
float exposureFromColorDeviation(vec4 avgColor, vec4 rollingAvgColor) {
    float deltaL = luminosity(avgColor) - luminosity(rollingAvgColor);

    float adaptedness = 1.0 - clamp(adaptionSlowdown * abs(deltaL), 0.0, 1.0);

    float exposureExtreme = (deltaL > 0)
                           ? (exposure + maxOverexposure)
                           : (exposure - maxUnderexposure);

    return mix(exposureExtreme, exposure, adaptedness);
}


// The higher this value the more the vignette extends to the middle of the screen
const float vignetteExtent = 1.0;
// How black does it get? More intensity, more blackness
const float vignetteIntensity = 0.07;

vec4 vignettize(vec4 unvignetted, vec2 uv, float blackness) {
    uv *=  vec2(1.0, 1.0) - uv.yx;   //vec2(1.0)- uv.yx; -> 1.-u.yx; Thanks FabriceNeyret !

    float vig = uv.x*uv.y * 1.0; // multiply with sth for intensity

    vig = pow(vig, vignetteIntensity); // change pow for modifying the extend of the  vignette

    vec4 vignetteFactor = clamp(vec4(vig), 0.0, 1.0);
    vec4 vignetteColor = mix(rollingAvgColor, vec4(0.0, 0.0, 0.0, 1.0), blackness);

    return mix(vignetteColor, unvignetted, vignetteFactor);
}

void main() {

    color = hdrToClampedSRGB(
        keyBase,
        exposureFromColorDeviation(mix(rollingAvgColor, avgColor, 0.1), rollingAvgColor),
        texture(hdr, st) + texture(texture1, st),
        rollingAvgColor
    );
    color.a = 1.0;

    //color.rgb = fract(vec3(avgLuminosity, avgLuminosity, avgLuminosity));

    //float gamma = 1.0/2.2;
    //color.rgb = pow(color.rgb, vec3(1.0/gamma, 1.0/gamma, 1.0/gamma));

    //color = vec4(avgColor.rgb, 1.0);

    if(st.s < 0.05 && st.t < 0.03) {
        color = avgColor;

        if(st.s > 0.025) {
            color = rollingAvgColor;
        }
    } else {
        //color = fract(texture(hdr, st));
    }
	
    color = vignettize(color, st, 0.8);

    //color = mix(mix(rollingAvgColor, vec4(0.0, 0.0, 0.0, 1.0), 0.8) , color, clamp(vignetteFactor, 0.0, 1.0));
    //color *= vignetteFactor;
}
