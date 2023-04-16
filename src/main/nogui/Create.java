package main.nogui;

import Console.Colors;
import Console.ConsoleManager;
import Input.Keyboard;
import Input.MultipleChoice;
import app.use_cases.CreateLibrary;
import core.entities.CEP;
import core.entities.Library;
import core.entities.Logradouro;
import exceptions.InvalidValueException;
import infrastructure.providers.api.CEPProvider;
import infrastructure.repositories.jackson.LibraryRepository;

public class Create {
	public final CEPProvider API_CEP;
	public final CreateLibrary REPOSITORY;

	public Create(LibraryRepository repository, CEPProvider apiCep) {
		this.API_CEP = apiCep;
		this.REPOSITORY = new CreateLibrary(repository);
	}

	public void run() {
		ConsoleManager.clear();
		ConsoleManager.println("Adicionar biblioteca", Colors.GREEN);
		Library library = new Library();
		try {
			ConsoleManager.print(" Nome: ", Colors.CYAN);
			library.setName(Keyboard.getString());
			ConsoleManager.print(" Email: ", Colors.CYAN);
			library.setEmail(Keyboard.getString());
			ConsoleManager.print(" Complemento: ", Colors.CYAN);
			library.setComplement(Keyboard.getString());
			ConsoleManager.print(" Cep: ", Colors.CYAN);
			library.setCep(Keyboard.getString());
			ConsoleManager.print(" Numbero: ", Colors.CYAN);
			library.setNumber(Integer.parseInt(Keyboard.getString()));
		}
		catch (InvalidValueException e) {
			App.printError(e);
			return;
		}

		try {
			Logradouro l = API_CEP.getAddress(CEP.parseCep(library.getCep()));
			ConsoleManager.println(App.formatLibrary(library, l));
			
			Keyboard.multipleChoiceChar(new MultipleChoice<Character>() {
				@Override
				public boolean isCorrect(Character choice) { return "SsNn".indexOf(choice) != -1; }

				@Override
				public void handleIncorrect(Character choice) {
					ConsoleManager.println("Opção inválida!", Colors.RED);
				}

				@Override
				public void handle(Character choice) {
					switch(choice) {
						case 's':
							break;
						case 'n':
							App.printError("Operação cancelada!");
							
					}
				}
			});
		}
		catch (Exception e) {
			App.printError("Esse CEP não existe!\n tente novamente com um CEP válido.");
			return;
		}

		try { REPOSITORY.createLibrary(library); }
		catch (Exception e) { App.printError(e); }
	}
}
