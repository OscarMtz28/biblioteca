const API_USUARIOS = 'http://localhost:8081/biblioteca/usuarios';
const API_LIBROS = 'http://localhost:8082/biblioteca/libros';
const API_PRESTAMOS = 'http://localhost:8083/biblioteca/prestamos';

// Inicializar la aplicación
document.addEventListener('DOMContentLoaded', () => {
    cargarUsuarios();
    cargarLibros();
    
    document.getElementById('formUsuario').addEventListener('submit', crearUsuario);
    document.getElementById('formLibro').addEventListener('submit', crearLibro);
    document.getElementById('formPrestamo').addEventListener('submit', solicitarPrestamo);
    
    // Listeners for tabs to refresh data if necessary
    document.getElementById('usuarios-tab').addEventListener('click', cargarUsuarios);
    document.getElementById('catalogo-tab').addEventListener('click', cargarLibros);
    document.getElementById('resumen-tab').addEventListener('click', () => {
        cargarUsuarios();
        cargarLibros();
    });
});

// ========================
// USUARIOS
// ========================
async function cargarUsuarios() {
    try {
        const res = await fetch(API_USUARIOS);
        const usuarios = await res.json();
        
        document.getElementById('totalUsuarios').innerText = usuarios.length;
        
        const lista = document.getElementById('listaUsuarios');
        const select = document.getElementById('selectUsuario');
        const selectConsulta = document.getElementById('selectUsuarioConsulta');
        
        lista.innerHTML = '';
        select.innerHTML = '<option value="">Seleccione Usuario...</option>';
        selectConsulta.innerHTML = '<option value="">Seleccione Usuario...</option>';
        
        usuarios.forEach(u => {
            lista.innerHTML += `<li class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    <strong>${u.nombre}</strong><br>
                    <small class="text-muted"><a href="mailto:${u.email}">${u.email}</a></small>
                </div>
                <span class="badge ${u.penalizado ? 'bg-danger' : 'bg-success'} rounded-pill px-3 py-2">
                    Préstamos: ${u.prestamosActivos} | ${u.penalizado ? 'Penalizado' : 'OK'}
                </span>
            </li>`;
            
            const option = `<option value="${u.id}">${u.nombre}</option>`;
            select.innerHTML += option;
            selectConsulta.innerHTML += option;
        });
    } catch (e) {
        console.error("Error cargando usuarios:", e);
    }
}

async function crearUsuario(e) {
    e.preventDefault();
    const nombre = document.getElementById('nombreUsuario').value;
    const email = document.getElementById('emailUsuario').value;
    
    await fetch(API_USUARIOS, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, email })
    });
    
    document.getElementById('formUsuario').reset();
    cargarUsuarios();
    alert("Usuario registrado.");
}

// ========================
// LIBROS
// ========================
async function cargarLibros() {
    try {
        const res = await fetch(API_LIBROS);
        const libros = await res.json();
        
        document.getElementById('totalLibros').innerText = libros.length;
        
        const lista = document.getElementById('listaLibros');
        const select = document.getElementById('selectLibro');
        
        lista.innerHTML = '';
        select.innerHTML = '<option value="">Seleccione Libro...</option>';
        
        libros.forEach(l => {
            lista.innerHTML += `<li class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    <strong>${l.titulo}</strong> <span class="text-muted">por ${l.autor}</span><br>
                    <small>ISBN: ${l.isbn}</small>
                </div>
                <div class="d-flex align-items-center">
                    <span class="badge ${l.inventarioDisponible > 0 ? 'bg-primary' : 'bg-warning text-dark'} rounded-pill me-3 px-3 py-2">
                        Stock: ${l.inventarioDisponible} / ${l.inventarioTotal}
                    </span>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarLibro(${l.id})">Borrar</button>
                </div>
            </li>`;
            
            if (l.inventarioDisponible > 0) {
                select.innerHTML += `<option value="${l.id}">${l.titulo}</option>`;
            }
        });
    } catch (e) {
        console.error("Error cargando libros:", e);
    }
}

async function crearLibro(e) {
    e.preventDefault();
    const titulo = document.getElementById('tituloLibro').value;
    const autor = document.getElementById('autorLibro').value;
    const isbn = document.getElementById('isbnLibro').value;
    const inventario = parseInt(document.getElementById('invLibro').value);
    
    await fetch(API_LIBROS, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ titulo, autor, isbn, inventarioTotal: inventario, inventarioDisponible: inventario })
    });
    
    document.getElementById('formLibro').reset();
    cargarLibros();
    alert("Libro agregado al catálogo.");
}

async function eliminarLibro(id) {
    if (confirm("¿Seguro que deseas eliminar este libro del catálogo de manera permanente?")) {
        await fetch(`${API_LIBROS}/${id}`, { method: 'DELETE' });
        cargarLibros();
    }
}

// ========================
// PRESTAMOS
// ========================
async function solicitarPrestamo(e) {
    e.preventDefault();
    const usuarioId = document.getElementById('selectUsuario').value;
    const libroId = document.getElementById('selectLibro').value;
    
    try {
        const res = await fetch(API_PRESTAMOS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usuarioId, libroId })
        });
        
        if (res.ok) {
            alert("Préstamo registrado exitosamente!");
            cargarUsuarios();
            cargarLibros();
            
            document.getElementById('formPrestamo').reset();
            
            // Auto-refresh lista de préstamos para este usuario
            document.getElementById('selectUsuarioConsulta').value = usuarioId;
            consultarPrestamos();
        } else {
            const err = await res.text();
            alert("Error: " + err);
        }
    } catch (e) {
        alert("Error de conexión al solicitar préstamo");
    }
}

async function consultarPrestamos() {
    const usuarioId = document.getElementById('selectUsuarioConsulta').value;
    const lista = document.getElementById('listaPrestamos');
    
    if (!usuarioId) {
        lista.innerHTML = '<li class="list-group-item text-muted">Seleccione un usuario para ver sus préstamos.</li>';
        return;
    }
    
    try {
        const res = await fetch(`${API_PRESTAMOS}/usuario/${usuarioId}`);
        const prestamos = await res.json();
        
        lista.innerHTML = '';
        
        if (prestamos.length === 0) {
            lista.innerHTML = '<li class="list-group-item text-muted">No hay préstamos activos para este usuario.</li>';
            return;
        }
        
        for (const p of prestamos) {
            // Obtain book info beautifully
            let infoLibro = `Libro ID: #${p.libroId}`;
            try {
                const libRes = await fetch(`${API_LIBROS}/${p.libroId}`);
                if (libRes.ok) {
                    const libro = await libRes.json();
                    infoLibro = `<strong>${libro.titulo}</strong> <span class="text-muted">(${libro.autor})</span>`;
                }
            } catch (e) {}
            
            lista.innerHTML += `
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    ${infoLibro}<br>
                    <small class="text-secondary">Fecha de Préstamo: ${p.fechaPrestamo} &middot; Estado: <span class="badge bg-info">${p.estado}</span></small>
                </div>
                <button class="btn btn-outline-success" onclick="devolverLibro(${p.id})">Marcar Devuelto</button>
            </li>`;
        }
    } catch (e) {
        console.error("Error consultando préstamos:", e);
    }
}

async function devolverLibro(prestamoId) {
    if (confirm("¿Confirmar que el libro fue devuelto por el usuario?")) {
        try {
            const res = await fetch(`${API_PRESTAMOS}/${prestamoId}/devolucion`, {
                method: 'PUT'
            });
            
            if (res.ok) {
                alert("Libro devuelto exitosamente");
                cargarUsuarios();
                cargarLibros();
                consultarPrestamos();
            } else {
                alert("Hubo un error al devolver el libro.");
            }
        } catch (e) {
            alert("Error de conexión");
        }
    }
}
