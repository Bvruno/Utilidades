
    $(document).ready(function() {
        $('.btn').on('click', function() {
            var imageSrc = $(this).data('img');
            $('#modalImage').attr('src', imageSrc);
        });
    });

    document.querySelector('.custom-file-input').addEventListener('change', function(e) {
        var fileName = document.getElementById("customFile").files[0].name;
        var nextSibling = e.target.nextElementSibling
        nextSibling.innerText = fileName
    });

    function changeState(btn, newState) {
        var id = btn.dataset.id;

        fetch('/api/responses/' + id + '/state', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: newState,
        })
        .then(function(response) {
            return response.json();
        })
        .then(function(response) {
            if (response.state) {
                // Actualiza el estado en la interfaz de usuario
                var row = btn.parentElement;
                var stateCell = row.querySelector('td:nth-child(6)');
                stateCell.innerText = newState;

                // Actualiza los botones
                var acceptButtons = row.querySelectorAll('.btn-success');
                var rejectButtons = row.querySelectorAll('.btn-danger');
                if (newState === 'ACCEPTED') {
                    if (acceptButton) acceptButton.style.display = 'none';
                    if (rejectButton) rejectButton.style.display = '';
                } else if (newState === 'REJECTED') {
                    if (acceptButton) acceptButton.style.display = '';
                    if (rejectButton) rejectButton.style.display = 'none';
                } else if (newState === 'PENDING') {
                    if (acceptButton) acceptButton.style.display = '';
                    if (rejectButton) rejectButton.style.display = 'none';
                }
            } else {
                console.error('Error updating state');
            }
        });
    }

    // Obtén los campos de entrada del formulario de búsqueda
    var operationCodeInput = document.getElementById('operationCode');
    var recipientInput = document.getElementById('recipient');
    var dateInput = document.getElementById('date');
    var amountInput = document.getElementById('amount');
    var stateInput = document.getElementById('state');

    // Obtén las filas de la tabla
    var rows = document.querySelectorAll('table tbody tr');

    // Agrega un evento de escucha a cada campo de entrada
    operationCodeInput.addEventListener('input', filterRows);
    recipientInput.addEventListener('input', filterRows);
    dateInput.addEventListener('input', filterRows);
    amountInput.addEventListener('input', filterRows);
    stateInput.addEventListener('input', filterRows);

    function filterRows() {
        // Obtén los valores actuales de los campos de entrada
        var operationCode = operationCodeInput.value.toLowerCase();
        var recipient = recipientInput.value.toLowerCase();
        var date = dateInput.value;
        var amount = amountInput.value;
        var state = stateInput.value.toLowerCase();

        // Filtra las filas de la tabla
        rows.forEach(function(row) {
            var operationCodeCell = row.querySelector('td:nth-child(1)').innerText.toLowerCase();
            var recipientCell = row.querySelector('td:nth-child(2)').innerText.toLowerCase();
            var dateCell = row.querySelector('td:nth-child(3)').innerText;
            var amountCell = row.querySelector('td:nth-child(4)').innerText;
            var stateCell = row.querySelector('td:nth-child(5)').innerText.toLowerCase();

            if ((operationCode && operationCodeCell.includes(operationCode)) ||
                (recipient && recipientCell.includes(recipient)) ||
                (date && dateCell.includes(date)) ||
                (amount && amountCell.includes(amount)) ||
                (state && stateCell.includes(state))) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }