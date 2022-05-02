# Блог

## Обзор
Каждый пользователь, после прохождении регистрации
получает доступ к публикации своих постов и просмотру постов других пользователей

## Функциональность

* Регистрация аккаунта 

* Визуальное представление статей пользователей
   * Поиск по названию статьи

* Просмотр статьи
   * Редактирование статьи
    (только для автора)
   
* Личный кабинет
   * Возможность изменить пароль и email
   * Подписка на пользователя
   * Просмотр статей пользователя
   * Просмотр подписок/подписчиков пользователя
   * Доступ к списку пользователей с возможностью их редактирования
   (только для администратора)
   
## Техническая составляющая
### Backend

* JDK 18

* Spring framework
  * Boot
  * Web
  * Security
  * Data

* Maven

* SQL
  * Postgresql
 
* Миграция БД - flyway

### Frontend

* Шаблонизатор - thymeleaf
* Сайт с шаблонами - bootstrap
 
### Тестирование

* JUnit
(Модульные и интеграционное тестирование)
* Mockito
* Hamcrest

### Контейниризация

* Docker
  * Compose
(Команда для запуска контейнера "docker-compose up")
