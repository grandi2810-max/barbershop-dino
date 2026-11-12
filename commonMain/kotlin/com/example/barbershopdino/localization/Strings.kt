package com.example.barbershopdino.localization

import com.example.barbershopdino.model.AppLanguage

object Strings {
    fun get(key: String, language: AppLanguage): String {
        val isEn = language == AppLanguage.EN
        return when (key) {
            // App Title & General
            "app_title" -> if (isEn) "BarberShop Dino" else "BarberShop Dino"
            "welcome" -> if (isEn) "Welcome!" else "Benvenuto!"
            "select_role_subtitle" -> if (isEn) "Welcome to BarberShop Dino" else "Benvenuto in BarberShop Dino"
            "i_am_client" -> if (isEn) "Login" else "Accedi"
            "login_btn" -> if (isEn) "Login" else "Accedi"
            "i_am_barber" -> if (isEn) "I am a Barber" else "Sono un Parrucchiere"
            "switch_role" -> if (isEn) "Switch Role" else "Cambia Ruolo"
            "language" -> if (isEn) "Language" else "Lingua"
            "theme" -> if (isEn) "Theme" else "Tema"
            "light_mode" -> if (isEn) "Light Mode" else "Modalità Chiara"
            "dark_mode" -> if (isEn) "Dark Mode" else "Modalità Scura"
            "cancel" -> if (isEn) "Cancel" else "Annulla"
            "save" -> if (isEn) "Save" else "Salva"
            "confirm" -> if (isEn) "Confirm" else "Conferma"
            "continue_btn" -> if (isEn) "Continue" else "Continua"
            "close" -> if (isEn) "Close" else "Chiudi"
            "optional" -> if (isEn) "Optional" else "Opzionale"
            "not_required" -> if (isEn) "(not required)" else "(non obbligatorio)"
            "euro_symbol" -> "€"

            // Barber Setup
            "barber_setup_title" -> if (isEn) "Barber Profile" else "Profilo Parrucchiere"
            "barber_name" -> if (isEn) "Your Name" else "Il tuo Nome"
            "barber_name_hint" -> if (isEn) "e.g. Dino" else "es. Dino"
            "haircut_price" -> if (isEn) "Haircut Price (€)" else "Prezzo del Taglio (€)"
            "estimated_duration" -> if (isEn) "Estimated Duration" else "Tempo Stimato"
            "minutes" -> if (isEn) "minutes" else "minuti"

            // Client Setup
            "client_setup_title" -> if (isEn) "Client Registration" else "Registrazione Cliente"
            "first_name" -> if (isEn) "First Name" else "Nome"
            "last_name" -> if (isEn) "Last Name" else "Cognome"
            "age" -> if (isEn) "Age" else "Età"
            "how_old_are_you" -> if (isEn) "How old are you?" else "Quanti anni hai?"
            "when_is_your_birthday" -> if (isEn) "When is your birthday? (Day / Month)" else "Quando compi gli anni? (Giorno e Mese in numeri)"
            "birthday_label" -> if (isEn) "Birthday Date (DD/MM)" else "Data Compleanno (GG/MM)"
            "birthday_day_hint" -> if (isEn) "Day (e.g. 15)" else "Giorno (es. 15)"
            "birthday_month_hint" -> if (isEn) "Month (e.g. 01)" else "Mese (es. 01)"

            // Barber Dashboard / Calendar
            "barber_dashboard" -> if (isEn) "Barber Dashboard" else "Pannello Parrucchiere"
            "monthly_schedule" -> if (isEn) "Monthly Calendar" else "Tabella del Mese"
            "select_date" -> if (isEn) "Select Date" else "Seleziona Data"
            "appointments_for" -> if (isEn) "Appointments for" else "Appuntamenti per il"
            "no_appointments" -> if (isEn) "No appointments booked for this day." else "Nessun appuntamento prenotato per questo giorno."
            "booked_clients" -> if (isEn) "Booked Clients" else "Clienti Prenotati"
            "mark_completed" -> if (isEn) "Mark as Completed" else "Segna come Completato"
            "trigger_check" -> if (isEn) "Simulate 30 min check" else "Simula Notifica 30 min Prima"
            "status_confirmed" -> if (isEn) "Confirmed" else "Confermato"
            "status_postponed" -> if (isEn) "Postponed +30m" else "Posticipato di 30 min"
            "status_cancelled" -> if (isEn) "Cancelled" else "Annullato"
            "status_completed" -> if (isEn) "Completed" else "Completato"

            // Barber 30 min prompt
            "barber_prompt_title" -> if (isEn) "Upcoming Appointment Alert!" else "Notifica Appuntamento In Arrivo!"
            "barber_prompt_desc" -> if (isEn)
                "An appointment is scheduled in 30 minutes. Can you finish the previous client on time or do you need another 30 minutes?"
            else
                "Hai un appuntamento tra 30 minuti. Riesci a finire in tempo il cliente precedente o hai bisogno di un'altra mezz'ora per finire?"
            "on_time" -> if (isEn) "I am on time" else "Riesco in tempo"
            "need_30_min" -> if (isEn) "I need +30 minutes" else "Ho bisogno di un'altra mezz'ora"

            // Client Dashboard & Booking
            "client_dashboard" -> if (isEn) "Book Haircut" else "Prenota Taglio"
            "my_appointments" -> if (isEn) "My Appointments" else "I Miei Appuntamenti"
            "book_new" -> if (isEn) "New Booking" else "Nuova Prenotazione"
            "available_slots" -> if (isEn) "Available Time Slots" else "Orari Disponibili"
            "schedule_type_standard" -> if (isEn) "🕒 Standard Schedule (16:00 - 20:00)" else "🕒 Orario Ordinario (16:00 - 20:00)"
            "schedule_type_summer" -> if (isEn) "☀️ Summer Schedule (14:00 - 20:00)" else "☀️ Orario Estivo (14:00 - 20:00)"
            "schedule_sunday_closed" -> if (isEn) "🔴 Sunday Closed (No bookings)" else "🔴 Domenica Chiuso (Non prenotabile)"
            "barber_code_label" -> if (isEn) "Barber Access Code" else "Codice Accesso Parrucchiere"
            "barber_code_hint" -> if (isEn) "Enter code (e.g. 1234)" else "Inserisci codice (es. 1234)"
            "barber_code_dialog_title" -> if (isEn) "Barber Access Code" else "Codice Accesso Parrucchiere"
            "barber_code_incorrect" -> if (isEn) "Incorrect code! Try again." else "Codice errato! Riprova."
            "barber_icon_access_hint" -> if (isEn) "Tap 💈 logo icon to access Barber mode" else "Tocca l'icona 💈 per accedere come Parrucchiere"
            "slot_booked_anonymous" -> if (isEn) "Booked" else "Occupato"
            "slot_available" -> if (isEn) "Available" else "Disponibile"
            "confirm_booking" -> if (isEn) "Confirm Booking" else "Conferma Prenotazione"
            "booking_success" -> if (isEn) "Appointment successfully booked!" else "Appuntamento prenotato con successo!"
            "barber_info" -> if (isEn) "Barber Info" else "Info Parrucchiere"
            "price_tag" -> if (isEn) "Price" else "Prezzo"
            "duration_tag" -> if (isEn) "Duration" else "Durata"

            // Client Postponed Alert
            "client_alert_title" -> if (isEn) "Appointment Postponed!" else "Appuntamento Posticipato!"
            "client_alert_desc" -> if (isEn)
                "The barber has postponed your appointment by 30 minutes. What would you like to do?"
            else
                "Il parrucchiere ha posticipato di mezz'ora il tuo appuntamento. Cosa desideri fare?"
            "keep_appointment" -> if (isEn) "Accept & Go to Appointment" else "Accetta e Vai all'Appuntamento"
            "cancel_appointment" -> if (isEn) "Cancel Appointment" else "Annulla Appuntamento"
            "cancel_reason_label" -> if (isEn) "Reason for cancellation (optional):" else "Motivo dell'annullamento (non obbligatorio):"
            "cancel_reason_hint" -> if (isEn) "Type reason here..." else "Scrivi il motivo qui..."

            // Reviews
            "reviews_title" -> if (isEn) "Reviews & Ratings" else "Recensioni e Valutazioni"
            "leave_review" -> if (isEn) "Leave a Review" else "Lascia una Recensione"
            "review_dialog_title" -> if (isEn) "Rate your Haircut Experience" else "Valuta la tua Esperienza"
            "star_rating_label" -> if (isEn) "Rating (0.5 to 5 stars):" else "Valutazione (da 0.5 a 5 stelle):"
            "review_text_label" -> if (isEn) "Review text (optional):" else "Testo della recensione (opzionale):"
            "review_text_hint" -> if (isEn) "Tell us about your haircut..." else "Raccontaci com'è andato il taglio..."
            "submit_review" -> if (isEn) "Submit Review" else "Invia Recensione"
            "average_rating" -> if (isEn) "Average Rating" else "Media Recensioni"
            "no_reviews" -> if (isEn) "No reviews yet." else "Nessuna recensione ancora."

            else -> key
        }
    }
}
