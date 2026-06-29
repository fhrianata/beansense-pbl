# =============================================
# Dockerfile — BeanSense Web (Pure HTML + Nginx)
# Build context: ROOT project (beansense-backend-fixed/)
#
# FIX: context sekarang adalah root ".", bukan "./web"
# Karena folder web/ ada di root project, nginx/ juga di root.
# =============================================

FROM nginx:1.27-alpine

RUN rm -rf /usr/share/nginx/html/* \
    && rm -f /etc/nginx/conf.d/default.conf

# nginx.conf ada di folder nginx/ (relatif terhadap context = root)
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Konten web ada di folder web/ (relatif terhadap context = root)
COPY web/ /usr/share/nginx/html/

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD wget -qO- http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
