document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('login-form');

  loginForm.addEventListener('submit', async (event) => {
    event.preventDefault(); // Prevent the default form submission

    const formData = new FormData(loginForm);

    try {
      const response = await fetch(loginForm.action, {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        console.log('An error occurred:', response.status);

        const errorSpan = document.createElement('span');
        errorSpan.textContent = 'Invalid username or password';
        errorSpan.style.color = 'red';

        // You can append the error span to a specific element on the page
        // For example, assuming you have a div with id "errorContainer":
        const errorContainer = document.getElementById('error-wrapper');
        errorContainer.innerHTML = ''; // Clear previous errors
        errorContainer.appendChild(errorSpan);
      } else {
        console.log('Success!');
      }
    } catch (error) {
      console.error('An error occurred:', error);
    }
  });
});
