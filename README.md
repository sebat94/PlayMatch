# PlayMatch

Aplicación móvil de citas basada en el estilo de Tinder con soporte para Android >= 4.4.

El login/registro de la app se realiza a través de la SMS Verification API de google. Para ello se ha contratado un servicio en www.twilio.com de mensajería para poder enviar el código de verificación a las peticiones realizadas.

Al acceder a la aplicación se recogerá tu ubicación en tiempo real para poder filtrar gente de tus alrededores.

Tendremos 3 áreas de navegación. Mediante un ViewPager, tendremos como página principal o central un sistema de cards al estilo de tinder utilizando para ello la librería de yuyakaido y al clicar sobre la card actual se superpondrá un fragment con transición para la visualización de los datos del perfil del usuario seleccionado. Podrás dar Dislike/Like/Rewind/SuperLike con los botones de la parte inferior o desplazando las cartas a la derecha(Like) o a la izquierda(Dislike).

Volviendo al ViewPager, si desplazamos el dedo por la pantalla hacia la derecha, aparecerá nuestra imágen y datos de perfil de usuario. Se podrán ver 2 botones, "Ajustes" y "Editar información".

Al pulsar el botón de "Ajustes" podremos definir nuestras búsquedas de personas como el género(hombre, mujer, otros), Distancia máxima (Km), Rango de edad(mín. 18 años).

Al pulsar el botón de "Editar información" podremos agregar/eliminar hasta un máximo de 9 imágenes para el usuario. Se podrá insertar una imágen con la cámara de fotos o se podrá subir una o varias imágenes al mismo tiempo si las insertas desde tu galería de fotos. Además en esta misma ventana podrás editar tu información personal como el acerca de mi, puesto de trabajo, empresa para la que trabajas y escuela, todos estos campos serán guardados tras una espera de medio segundo tras dejar de escribir.

Por último, en el ViewPager, si desplazamos el dedo por la pantalla hacia la izquierda, veremos como aparece la lista de chats con las personas que hemos hecho match. Si seleccionas uno de ellos se abrirá la ventana del chat, donde podrás hablar en tiempo real con esa persona utilizando la librería de Socket IO.

Librerías Destacadas
----------------------

https://developers.google.com/identity/sms-retriever/overview
https://github.com/yuyakaido/CardStackView
https://github.com/ReactiveX/RxJava
https://socket.io
https://developer.android.com/training/volley?hl=es-419
https://github.com/google/gson
https://github.com/bumptech/glide
https://www.twilio.com/
https://github.com/Jay-Goo/RangeSeekBar

Tecnologías
-------------

Android, RxJava2, SocketIO, Java 8, Node.js + Express, ES6, TypeScript, MySQL
