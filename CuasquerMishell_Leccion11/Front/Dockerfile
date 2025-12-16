# Usar imagen base de nginx para servir archivos estáticos
FROM nginx:alpine

# Eliminar el archivo de configuración por defecto
RUN rm /etc/nginx/conf.d/default.conf

# Copiar el archivo de configuración personalizado
COPY nginx.conf /etc/nginx/conf.d/

# Copiar los archivos del frontend
COPY . /usr/share/nginx/html

# Exponer el puerto 80
EXPOSE 80

# Iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
