#version 330

/** Rendered image in (unclamped) HDR */
uniform sampler2D hdr;
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

void main() {
    vec4 hdrColor = texture(hdr, st);

    float stepX = 0.001;
    float stepY = 0.001;

    vec4 neighbourHoodColorSum = vec4(0.0, 0.0, 0.0, 0.0);

    neighbourHoodColorSum += texture(hdr, st + vec2(stepX, 0));
    neighbourHoodColorSum += texture(hdr, st + vec2(-stepX, 0));
    neighbourHoodColorSum += texture(hdr, st + vec2(0, stepY));
    neighbourHoodColorSum += texture(hdr, st + vec2(0, -stepY));

    neighbourHoodColorSum += texture(hdr, st + vec2(-stepX, -stepY));
    neighbourHoodColorSum += texture(hdr, st + vec2(stepX, -stepY));
    neighbourHoodColorSum += texture(hdr, st + vec2(stepX, stepY));
    neighbourHoodColorSum += texture(hdr, st + vec2(-stepX, stepY));

    float neighbourHoodLuminosity = luminosity(neighbourHoodColorSum / 8);
    float fragLuminosity = luminosity(hdrColor);
    const float bloominess = 1000.0;
    float bloomFactor = 1.0;

    if(neighbourHoodLuminosity > fragLuminosity) {
        bloomFactor += (neighbourHoodLuminosity - fragLuminosity) * bloominess;
    }

    hdrColor *= bloomFactor;

    color = hdrToClampedSRGB(
        keyBase,
        exposureFromColorDeviation(mix(rollingAvgColor, avgColor, 0.1), rollingAvgColor),
        hdrColor,
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


}
