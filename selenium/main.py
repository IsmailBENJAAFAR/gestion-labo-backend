import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select

driver = webdriver.Firefox()
driver.implicitly_wait(2)
if len(sys.argv) < 2:
    print("Veuillez donner l'url pour le service de gestion de laboratoire")
    exit(1)
url = sys.argv[1]
print(f"url: {url}")

def initial_load():
    driver.get(url)
    title = driver.title
    print(f"Titre: {title}")

def go_to_connection_page():
    print("Appui sur le bouton se connecter")
    button = driver.find_element(By.CLASS_NAME, "btn-primary")
    button.click()

def connect_user():
    user, password = "s@gmail.com", "123"
    print(f"Connexion avec utilisateur `{user}`, et mot de passe `{password}`")

    [email_input, password_input] = driver.find_elements(By.CLASS_NAME, "input")
    submit_button = driver.find_element(By.CLASS_NAME, "btn-primary")

    email_input.send_keys(user)
    password_input.send_keys(password)

    submit_button.click()

def go_to_examen_page():
    driver.get(f"{url}/dashboard/examen")
    button = driver.find_element(By.CLASS_NAME, "btn-primary")
    assert "Nouveau" in button.text, "Erreur bouton de creation non trouve"
    button.click()

def fill_new_exam_data():
    nom_input = driver.find_element(By.CLASS_NAME, "input")
    nom_input.send_keys("Examination")

    select_elements = driver.find_elements(By.CSS_SELECTOR, "select")
    for elem in select_elements:
        select = Select(elem)
        select.select_by_value("1")
    print("Informations d'exam remplies")

def submit_exam_creation():
    submit_button = driver.find_element(By.CLASS_NAME, "btn-primary")
    submit_button.click()

def run_selenium():
    initial_load()
    go_to_connection_page()
    connect_user()
    go_to_examen_page()
    fill_new_exam_data()
    submit_exam_creation()

run_selenium()
# TODO: will leave this commented for demonstration purposes
# driver.quit()
