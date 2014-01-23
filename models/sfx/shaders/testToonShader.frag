uniform sampler2D tex;
varying vec3 normal;

void main()
{
	float intensity;
	vec4 color;
	vec3 n = normalize(normal);
	
	intensity = dot(vec3(gl_LightSource[1].position),n);
	color = texture2D(tex,gl_TexCoord[0].st);
	
	/*if (intensity > 0.85)
		color = vec4(1.0,0.5,0.5,1.0);
	else if (intensity > 0.5)
		color = vec4(0.6,0.3,0.3,1.0);
	else if (intensity > 0.375)
		color = vec4(0.5,0.25,0.25,1.0);
	else if (intensity > 0.25)
		color = vec4(0.4,0.2,0.2,1.0);
	else if (intensity > 0.1)
		color = vec4(0.2,0.1,0.1,1.0);
	else
		color = vec4(0.1,0.05,0.05,1.0);
	*/
	
	if(intensity > 0.85)
		intensity = 0.85;
	else if(intensity > 0.5)
		intensity = 0.5;
	else if(intensity > 0.375)
		intensity = 0.375;
	else if(intensity > 0.25)
		intensity = 0.25;
	else if(intensity > 0.1)
		intensity = 0.1;
	
	color[0] *= intensity;
	color[1] *= intensity;
	color[2] *= intensity;
	
	gl_FragColor = color;
} 