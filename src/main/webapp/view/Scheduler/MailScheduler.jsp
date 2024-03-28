<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Mail Scheduler</title>
<style>
/* Default styles */
.content {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 20px;
}

#image-container {
    position: relative;
    top: -200px; /* Adjust the value according to the desired initial position */
    opacity: 0;
    animation: dropContent 1.5s cubic-bezier(0.42, 0, 0.58, 1) forwards;
}

#responsive-image {
    width: 75%;
    height: auto;
    display: block;
    margin: 0 auto;
}

.copy-right {
    text-align: center;
    opacity: 0;
    animation: fadeIn 1.5s ease-in-out forwards;
}

/* Media query for smaller screens */
@media only screen and (max-width: 600px) {
    .content {
        padding: 0 10px; /* Adjust padding for smaller screens */
    }
}

/* Keyframes for animation */
@keyframes dropContent {
    0% {
        top: -200px; /* Initial position */
        opacity: 0;
    }
    100% {
        top: 0; /* Final position */
        opacity: 1;
    }
}

@keyframes fadeIn {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
</style>
</head>
<body>
<h1 style="text-align: center; color:blue;"> Welcome to Mail Scheduler </h1>

<div class="content">
    <div id="image-container">
        <img id="responsive-image" src="view/images/home.jpg" title="home" /> 
        <div class="copy-right"> 
            <p>&copy; <script type="text/javascript">
            document.write(new Date().getFullYear());
            </script> Ohh. All Rights Reserved | Design by VTS</p> 
        </div>
    </div>
</div>

</body>
</html>
